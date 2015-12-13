package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.content.Context;

import java.io.CharConversionException;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class FileDownloader {
    private static final String TAG = "WP_FileDownloader";
    private Context context;

    private boolean exit;       //停止下载
    private int fileSize;
    private DownloadThread[] threads;

    private File saveFile;

    private URL downUrl;

    private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();
    private int block;//每条线程下载的长度;

    private String downloadUrl;

    public boolean isExit() {
        return exit;
    }

    //constructor
    public FileDownloader(Context context, String downloadUrl,
                          File fileSaveDir, int threadNum) {
        try{
            this.context = context;
            this.downloadUrl = downloadUrl;
            //fileService = new FileService(this.context);
            URL url = new URL(this.downloadUrl);
            if(!fileSaveDir.exists()) fileSaveDir.mkdirs();//如果不存在目录，那就创建一个
            threads = new DownloadThread[threadNum];//
            HttpURLConnection http =
                    (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(5 * 1000); //5s
            http.setRequestMethod("GET");   //GET方法获得数据，很自然
            http.setRequestProperty("Charset","UTF-8");
            http.setRequestProperty("Connection","Keep-Alive");
            http.connect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
