package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class DownloadThread extends Thread{
    private static final String TAG = "WP_DownloadThread";
    private File saveFile;
    private URL downUrl;
    private int block;

    private int threadId = -1;

    public int getDownLength() {
        return downLength;
    }

    private int downLength = 0;

    public boolean isFinish() {
        return finish;
    }

    private boolean finish = false;
    private FileDownloader downloader;

    public DownloadThread(FileDownloader downloader, URL downUrl,
                          File saveFile, int block, int downLength, int threadId){
        this.downUrl = downUrl;
        this.saveFile = saveFile;
        this.block = block;
        this.downloader = downloader;
        this.threadId = threadId;
        this.downLength = downLength;
    }

    @Override
       public void run() {
        if(downLength<block) {//未下载完成
            try{
                HttpURLConnection http =
                        (HttpURLConnection) downUrl.openConnection();
                http.setConnectTimeout(5 * 1000); //5s
                http.setRequestMethod("GET");   //GET方法获得数据，很自然
                http.setRequestProperty("Charset","UTF-8");
                int startPos = block * (threadId-1) + downLength;//计算开始位置
                int endPos = block * threadId -1;               //该block结束位置
                http.setRequestProperty("Range","bytes="+startPos+"-"+endPos);
                //TODO：设置浏览器类型

                http.setRequestProperty("Connection", "Keep-Alive");//设置为持久连接
                InputStream inputStream = http.getInputStream();
                byte[] buffer = new byte[1024];
                int offset = 0;
                Log.d(TAG,"Thread " + this.threadId+ " start downloading from position: "+startPos);

                //RandomAccessFile更容易定位操作
                RandomAccessFile threadFile = new RandomAccessFile(this.saveFile, "rwd");
                Log.d("WPTEST_ADD", String.valueOf(saveFile));
                threadFile.seek(startPos);
                while(!downloader.isExit() &&
                        (offset = inputStream.read(buffer,0,1024)) != -1){
                    threadFile.write(buffer, 0, offset);
                    downLength += offset;
                    //TODO:更新线程已经下载位置
                    downloader.update(this.threadId, downLength);
                    downloader.append(offset);

                }
                threadFile.close();
                inputStream.close();
                Log.d(TAG, "Thread " + this.threadId + " download finished!");
                finish = true;
            } catch (IOException e) {
                downLength = -1;
                Log.d(TAG,"Thread "+ this.threadId +"  Failed!  -_-!");
                e.printStackTrace();
            }
        }
    }
}
