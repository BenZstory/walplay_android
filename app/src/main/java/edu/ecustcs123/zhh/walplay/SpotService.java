package edu.ecustcs123.zhh.walplay;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import edu.ecustcs123.zhh.walplay.DownloadUtils.DownloadProgressListener;
import edu.ecustcs123.zhh.walplay.DownloadUtils.FileDownloader;

public class SpotService extends Service {
    private DownloadTask task;


    public SpotService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int spotId = intent.getIntExtra("spotId",-2);
        if(spotId == -1){
            //remoteMusic, Download first
            String url = intent.getStringExtra("downloadUrl");
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // File savDir =
                // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                // 保存路径
                File savDir = Environment.getExternalStorageDirectory();
                Log.d("TEST---------", String.valueOf(savDir));
                download(url,savDir);

                Intent intent2 = new Intent();
                intent2.putExtra("isRemote",true);
                intent2.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
                intent2.putExtra("path",savDir);
                intent2.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                intent2.setPackage("edu.ecustcs123.zhh.walplay");
                startService(intent2);

            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.sdcarderror, Toast.LENGTH_LONG).show();
            }

        }else{
            Intent intent2  = new Intent();
            intent2.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            intent2.putExtra("listPos", spotId);
            intent2.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent2.setPackage("edu.ecustcs123.zhh.walplay");
            startService(intent2);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 广播更新播放时间
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                /*if (mediaPlayer != null) {
                    playingInfo.setCurrentTime(mediaPlayer.getCurrentPosition());
                    BroadcastUpdate(AppConstant.UPDATE_TYPE.MUSIC_CURRENT);*//*
                    handler.sendEmptyMessageDelayed(1, 1000);
                }*/
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private void exit() {
        if (task != null)
            task.exit();
    }

    private final class DownloadTask implements Runnable {
        private String path;
        private File saveDir;
        private FileDownloader loader;

        public DownloadTask(String path, File saveDir) {
            this.path = path;
            this.saveDir = saveDir;
        }

        /**
         * 退出下载
         */
        public void exit() {
            if (loader != null)
                loader.setExit(true);
        }

        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
            @Override
            public void onDownloadSize(int size) {
                Log.d(AppConstant.LOG.DownloadTest, String.valueOf(size));
                Message msg = new Message();
                msg.what = 1;
                msg.getData().putInt("size", size);
                handler.sendMessage(msg);
            }
        };

        public void run() {
            try {
                // 实例化一个文件下载器
                loader = new FileDownloader(getApplicationContext(), path,
                        saveDir, 1);
                // 设置进度条最大值
                //progressBar.setMax(loader.getFileSize());
                Log.d(AppConstant.LOG.DownloadTest,"listener done!");
                loader.download(downloadProgressListener);
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(-1)); // 发送一条空消息对象
            }
        }
    }

    private void download(String path, File savDir) {
        task = new DownloadTask(path, savDir);
        new Thread(task).start();
    }
}
