package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class FileDownloader {
    private static final String TAG = "WP_FileDownloader";
    private Context context;
    private FileService fileService;
    private DownloadThread[] threads;

    public File getSaveFile() {
        return saveFile;
    }

    private File saveFile;
    private int downloadSize = 0;
    private boolean exit;       //停止下载

    public int getFileSize() {
        return fileSize;
    }

    private int fileSize;
    private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
    private int block;//每条线程下载的长度;
    private String downloadUrl;

    public boolean isExit() {
        return exit;
    }
    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public void append(int size){
        downloadSize += size;
    }

    public void update(int threadId, int downLength){
        data.put(threadId, downLength);
        fileService.update(downloadUrl, threadId, downLength);
    }

    /**
     * @param context
     * @param downloadUrl
     * @param fileSaveDir
     * @param threadNum
     */
    //constructor
    public FileDownloader(Context context, String downloadUrl,
                          File fileSaveDir, int threadNum) {
        try{
            this.context = context;
            this.downloadUrl = downloadUrl;
            fileService = new FileService(this.context);
            URL url = new URL(this.downloadUrl);
            if(!fileSaveDir.exists()) fileSaveDir.mkdirs();//如果不存在目录，那就创建一个
            threads = new DownloadThread[threadNum];//
            HttpURLConnection http =
                    (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(10 * 1000); //5s
            http.setRequestMethod("GET");   //GET方法获得数据，很自然
            http.setRequestProperty("Charset","UTF-8");
            http.setRequestProperty("Connection","Keep-Alive");
            http.connect();
            printResponseHeader(http);

            if(http.getResponseCode() == 200){
                this.fileSize = http.getContentLength();
                if(this.fileSize <= 0){
                    throw new RuntimeException("Unkown file size");
                }
                String filename = getFileName(http);//获取文件名
                saveFile = new File(fileSaveDir, filename);
                Log.d("WP_TEST_saveFilename", String.valueOf(saveFile));

                //获取下载记录
                Map<Integer, Integer> logdata = fileService.getData(downloadUrl);
                if(logdata.size()>0){//若确实有下载记录
                    for (Map.Entry<Integer,Integer> entry : logdata.entrySet()){
                        this.data.put(entry.getKey(),entry.getValue());
                    }
                }
                if (this.data.size() == this.threads.length) {// 下面计算所有线程已经下载的数据总长度
                    for (int i = 0; i < this.threads.length; i++) {
                        this.downloadSize += this.data.get(i + 1);
                    }
                }
                this.block = (this.fileSize % this.threads.length == 0) ?
                        this.fileSize / this.threads.length :
                        this.fileSize / this.threads.length + 1;
            }else{
                throw new RuntimeException("server no response");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取文件名也不是一件容易的事啊
     * @param http
     * @return
     */
    private String getFileName(HttpURLConnection http){
        String fileName = this.downloadUrl.substring(this.downloadUrl.lastIndexOf('/')+1);

        //倘若获取不到文件名
        if(fileName == null || "".equals(fileName.trim())){
            for(int i=0;;i++){
                String mine = http.getHeaderField(i);
                Log.d(TAG+"header",mine);
                if(mine == null)
                    break;
                if("content-disposition".equals(http.getHeaderFieldKey(i).toLowerCase())){
                    Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());//这个正则好奇怪啊...
                    if(m.find()) return m.group(1);
                }
            }
            fileName = UUID.randomUUID()+".tmp";//默认随机名
        }
        return fileName;
    }

    /**
     *
     * 开始下载文件
     * @return
     * @throws Exception
     */
    public int download(DownloadProgressListener listener) throws
            Exception{
        try{
            RandomAccessFile randOut = new RandomAccessFile(saveFile, "rw");
            if(fileSize>0){
                randOut.setLength(fileSize);
            }
            randOut.close();
            URL url = new URL(downloadUrl);
            if(data.size() != threads.length){
                data.clear();
                for(int i=0;i<threads.length;i++){
                    data.put(i+1,0);// 初始化每条线程已经下载的数据长度为0
                }
                downloadSize = 0;
            }
            for (int i=0;i<threads.length;i++){
                //开启线程，进行下载
                int downLength = this.data.get(i+1);
                if(downLength < this.block &&
                        downloadSize < fileSize){
                    threads[i] = new DownloadThread(
                            this, url, saveFile, block, data.get(i+1), i+1);
                    threads[i].setPriority(7);
                    threads[i].start();

                }else{
                    this.threads[i] = null;
                }
            }
            fileService.delete(downloadUrl);
            fileService.save(downloadUrl, data);
            boolean notFinish = true;
            while(notFinish){
                Thread.sleep(900);
                notFinish = false;//先假定全部线程已经下载完成
                for(int i=0;i<this.threads.length;i++){
                    if(threads[i]!=null && !threads[i].isFinish()){
                        notFinish = true;
                        if(threads[i].getDownLength() == -1){//下载失败，重新下载
                            threads[i] = new DownloadThread(
                                    this, url, saveFile, block, data.get(i + 1), i + 1);
                            threads[i].start();
                        }
                    }
                }
                if (listener != null)
                    listener.onDownloadSize(this.downloadSize);// 通知目前已经下载完成的数据长度
            }
            if(downloadSize == fileSize)
                fileService.delete(this.downloadUrl);//下载完成删除记录
        }catch (Exception e){
            Log.d(TAG, String.valueOf(e));
            throw new Exception("file download error");
        }
        return this.downloadSize;
    }

    /**
     * 获取Http响应头字段
     *
     * @param http
     * @return
     */
    public static Map<String, String> getHttpResponseHeader(
            HttpURLConnection http) {
        Map<String, String> header = new LinkedHashMap<String, String>();
        for (int i = 0;; i++) {
            String mine = http.getHeaderField(i);
            if (mine == null)
                break;
            header.put(http.getHeaderFieldKey(i), mine);
        }
        return header;
    }

    /**
     * 打印Http头字段
     *
     * @param http
     */
    public static void printResponseHeader(HttpURLConnection http) {
        Map<String, String> header = getHttpResponseHeader(http);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey() + ":" : "";
            print(key + entry.getValue());
        }
    }

    private static void print(String msg) {
        Log.i(TAG, msg);
    }

}
