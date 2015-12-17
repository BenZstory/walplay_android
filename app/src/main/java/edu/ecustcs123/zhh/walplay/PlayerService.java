package edu.ecustcs123.zhh.walplay;import android.annotation.SuppressLint;import android.app.Service;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;import android.content.IntentFilter;import android.media.MediaPlayer;import android.os.IBinder;import android.os.Handler;import android.os.Message;import android.util.Log;import java.util.List;/** * Created by BenZ on 2015/12/8. * zhengbin0320@gmail.com */@SuppressLint("NewApi")public class PlayerService extends Service {    private PlayingInfo playingInfo = new PlayingInfo();    private MediaPlayer mediaPlayer;    private boolean isProgressChanged = false;    private List<Mp3Info> mp3Infos;//音乐列表    private MyReceiver myReceiver;    private int playMode = AppConstant.PlayMode.MODE_ROUND;//播放模式，见appConstant.java    @Override    public IBinder onBind(Intent intent) {        return null;    }    @Override    public void onCreate() {        super.onCreate();        Log.d(AppConstant.LOG.Service_oncreate, "create");        mediaPlayer = new MediaPlayer();        mp3Infos = MusicListUtil.getMp3Infos(this);        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {            /**             * 当播放完成后如何操作，取决于播放模式Play_mode             */            @Override            public void onCompletion(MediaPlayer mp) {                if (playMode == AppConstant.PlayMode.MODE_REPEAT) {                    playingInfo.setCurrentTime(0);                    mediaPlayer.start();//单曲循环                } else if (playMode == AppConstant.PlayMode.MODE_ROUND) {                    //列表循环                    playingInfo.setListPos(playingInfo.getListPos() + 1);                    if (playingInfo.getListPos() > mp3Infos.size() - 1) playingInfo.setListPos(0);                    ReadyToPlay();                    play();                } else if (playMode == AppConstant.PlayMode.MODE_RANDOM) {                    playingInfo.setListPos((int) (Math.random() * (mp3Infos.size() - 1)));                    ReadyToPlay();                    play();                }            }            private void ReadyToPlay() {                BroadcastUpdate(AppConstant.UPDATE_TYPE.MUSIC_POS);                playingInfo.setUrl(mp3Infos.get(playingInfo.getListPos()).getUrl());                playingInfo.setCurrentTime(0);            }        });        myReceiver = new MyReceiver();        IntentFilter filter = new IntentFilter();        filter.addAction(AppConstant.ACTION.CTL_ACTION);        filter.addAction(AppConstant.ACTION.GET_SPOT_REFRESH);        registerReceiver(myReceiver, filter);    }    /**     * 广播更新播放时间     */    private Handler handler = new Handler() {        @Override        public void handleMessage(Message msg) {            if (msg.what == 1) {                if (mediaPlayer != null) {                    playingInfo.setCurrentTime(mediaPlayer.getCurrentPosition());                    BroadcastUpdate(AppConstant.UPDATE_TYPE.MUSIC_CURRENT);                    handler.sendEmptyMessageDelayed(1, 1000);                }            }        }    };    /**     * 负责向PlayerPanel传递更新     *     * @param type 更新类型     */    private void BroadcastUpdate(int type) {        Intent intent = new Intent();        switch (type) {            case AppConstant.UPDATE_TYPE.MUSIC_POS:                intent.setAction(AppConstant.ACTION.MUSIC_LISTPOS);                intent.putExtra("listPos", playingInfo.getListPos());                break;            case AppConstant.UPDATE_TYPE.MUSIC_CURRENT:                intent.setAction(AppConstant.ACTION.MUSIC_CURRENT);                intent.putExtra("currentTime", (int) playingInfo.getCurrentTime());//这里必须是int啊，我去，调试得太辛苦了！！！早知道就不设置成long了！！                break;            case AppConstant.UPDATE_TYPE.MUSIC_DURATION:                intent.setAction(AppConstant.ACTION.MUSIC_DURATION);                intent.putExtra("duration", (int) playingInfo.getDuration());                break;        }        sendBroadcast(intent);    }    @Override    public int onStartCommand(Intent intent, int flags, int startId) {        Log.d(AppConstant.LOG.Service_onStart, "start");//        Log.d("WP_playingInfo", String.valueOf(playingInfo));        playingInfo.setListPos(intent.getIntExtra("listPos", -1));        int msg = intent.getIntExtra("MSG", 0);        if (msg == AppConstant.PlayerMsg.PLAY_MSG) {//            Log.d("WP_playingInfo", "updating in Service!");            UpdatePlayingInfo(1);//根据listPos更新playingInfo            play();        } else if (msg == AppConstant.PlayerMsg.PAUSE_MSG) {            pause();        } else if (msg == AppConstant.PlayerMsg.STOP_MSG) {            stop();        } else if (msg == AppConstant.PlayerMsg.CONTINUE_MSG) {            resume();        } else if (msg == AppConstant.PlayerMsg.PROGRESS_CHANGE) {            playingInfo.setCurrentTime(intent.getIntExtra("progress", -1));            //Log.d(AppConstant.LOG.LOG_GETPROGRESSCHANGE, String.valueOf(currentTime));            isProgressChanged = true;            if (!playingInfo.isPause()) {                play();            }        }        return super.onStartCommand(intent, flags, startId);    }    public void UpdatePlayingInfo(int type) {        switch (type) {            case 1:                int pos = playingInfo.getListPos();                playingInfo.setUrl(mp3Infos.get(pos).getUrl());                playingInfo.setMusicTitle(mp3Infos.get(pos).getTitle());                playingInfo.setDuration(mp3Infos.get(pos).getDuration());                playingInfo.setCurrentTime(0);                CurrentInfo.CurrentName=mp3Infos.get(pos).getTitle();                CurrentInfo.CurrentArtist=mp3Infos.get(pos).getArtist();                break;        }    }    /**     * 播放必不可少的步骤     * position : 音乐流从哪开始     */    private void play() {        try {            //Log.d(AppConstant.LOG.LOG_CURRENTTIME + "_p", String.valueOf(currentTime));            isProgressChanged = false;            playingInfo.setIsPlaying(true);            mediaPlayer.reset();        //重置各项参数            mediaPlayer.setDataSource(playingInfo.getUrl());//获取文件uri            mediaPlayer.prepare();      //缓冲            mediaPlayer.setOnPreparedListener(new PreparedListener((int) playingInfo.getCurrentTime()));//注册监听器            handler.sendEmptyMessage(1);        //开始播放，启动返回播放进度的广播        } catch (Exception e) {            e.printStackTrace();        }    }    private void pause() {        if (mediaPlayer != null && mediaPlayer.isPlaying()) {            mediaPlayer.pause();            playingInfo.setIsPause(true);        }    }    private void resume() {        if (playingInfo.isPause()) {            //要判断是否在暂停的时候拖动过进度条            if (isProgressChanged) {                play();            } else {                mediaPlayer.start();            }            playingInfo.setIsPause(false);        }        playingInfo.setIsPlaying(true);    }    private void stop() {        if (mediaPlayer != null) {            mediaPlayer.stop();            try {                mediaPlayer.prepare();            } catch (Exception e) {                e.printStackTrace();            }        }    }    private final class PreparedListener implements MediaPlayer.OnPreparedListener {        private int currentTime;        public PreparedListener(int currentTime) {            this.currentTime = currentTime;        }        @Override        public void onPrepared(MediaPlayer mp) {//            Log.d(AppConstant.LOG.LOG_CURRENTTIME + "_pre", String.valueOf(currentTime));//            Log.d(AppConstant.LOG.LOG_DURATION, String.valueOf(playingInfo.getDuration()));            //传回歌曲长度信息            playingInfo.setDuration(mediaPlayer.getDuration());//            Log.d(AppConstant.LOG.test151212 + "", "Before broadcasing");            BroadcastUpdate(AppConstant.UPDATE_TYPE.MUSIC_DURATION);            //开始播放            if (currentTime > 0) {                mediaPlayer.seekTo(currentTime);            }            mediaPlayer.start();        }    }    public class MyReceiver extends BroadcastReceiver {        @Override        public void onReceive(Context context, Intent intent) {            String action = intent.getAction();            if (action.equals(AppConstant.ACTION.GET_SPOT_REFRESH)) {                //返回当前的相关信息                Intent refresh = new Intent(AppConstant.ACTION.SET_SPOT_REFRESH);                Log.d("WP_onCasting", String.valueOf(playingInfo));                refresh.putExtra(AppConstant.KEY.PARCELABLE_PLAYINGINFO, playingInfo);                sendBroadcast(refresh);            } else if (action.equals(AppConstant.ACTION.CTL_ACTION)) {                int mode = intent.getIntExtra("playMode", -1);                switch (mode) {                    case AppConstant.PlayMode.MODE_RANDOM:                        playMode = AppConstant.PlayMode.MODE_RANDOM;                        break;                    case AppConstant.PlayMode.MODE_ROUND:                        playMode = AppConstant.PlayMode.MODE_ROUND;                        break;                    case AppConstant.PlayMode.MODE_REPEAT:                        playMode = AppConstant.PlayMode.MODE_REPEAT;                        break;                }            }        }    }}