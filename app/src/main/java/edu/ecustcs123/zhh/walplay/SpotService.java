package edu.ecustcs123.zhh.walplay;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.util.UUID;
import edu.ecustcs123.zhh.walplay.DownloadUtils.DownloadProgressListener;
import edu.ecustcs123.zhh.walplay.DownloadUtils.FileDownloader;

public class SpotService extends Service {
    private DownloadTask task;
    private String savePath;
    public SpotService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int spotId = intent.getIntExtra("spotId",-2);
        if(spotId == -1){
            Log.d(AppConstant.LOG.RemoteTest+"id", String.valueOf(spotId));
            //remoteMusic, Download first
            String url = intent.getStringExtra("downloadUrl");
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // File savDir =
                // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                // 保存路径
                File savDir = Environment.getExternalStorageDirectory();
                download(url,savDir);
                savePath = savDir.getPath()+"/"+getFileName(url);
                Log.d(AppConstant.LOG.WPTEST+"__savePath", String.valueOf(savePath));
                Intent intent2 = new Intent();
                intent2.putExtra("isRemote",true);
                intent2.putExtra("MSG",AppConstant.PlayerMsg.PLAY_MSG);
                intent2.putExtra("path",savePath);
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
        private int fileSize;

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
                Log.d(AppConstant.LOG.WPDEBUG+"____dSize", String.valueOf(size));
                Intent intent = new Intent(AppConstant.ACTION.MUSIC_CACHE);
                float num = (float)size / (float)fileSize;
//                int percent = (int) (num * 100);
                intent.putExtra("percent",num);
                sendBroadcast(intent);
            }
        };

        public void run() {
            try {
                // 实例化一个文件下载器
                loader = new FileDownloader(getApplicationContext(), path,
                        saveDir, 1);
                this.fileSize = loader.getFileSize();
                savePath = loader.getSaveFile().toString();
                Log.d(AppConstant.LOG.DownloadTest,"listener done!");
                loader.download(downloadProgressListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void download(String path, File savDir) {
        task = new DownloadTask(path, savDir);
        new Thread(task).start();
    }

    private String getFileName(String downloadUrl){
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf('/')+1);
        //倘若获取不到文件名
        if(fileName == null || "".equals(fileName.trim())){
            fileName = UUID.randomUUID()+".tmp";//默认随机名
        }
        return fileName;
    }

}
