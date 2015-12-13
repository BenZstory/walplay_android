package edu.ecustcs123.zhh.walplay.DownloadUtils;

import android.content.Context;

import java.io.CharConversionException;
import java.io.File;
import java.net.URL;

/**
 * Created by BenZ on 15.12.13.
 * zhengbin0320@gmail.com
 */
public class FileDownloader {
    private static final String TAG = "FileDownloader";
    private Context context;

    private boolean exit;       //停止下载
    private int fileSize;

    private File saveFile;

    private URL downUrl;

    public boolean isExit() {
        return exit;
    }

    //constructor
    public FileDownloader() {
    }
}
