package edu.ecustcs123.zhh.walplay;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.Mp3Info;
import edu.ecustcs123.zhh.walplay.Utils.MusicListUtil;
import edu.ecustcs123.zhh.walplay.Utils.PlayingInfo;

public class PlayerPanelFragment extends android.support.v4.app.Fragment {
    public PlayingInfo playingInfo = new PlayingInfo();
    private SeekBar seekBar;
    private TextView tv_Title;
    private OnFragmentInteractionListener mListener;
    private ImageButton previousBtn;
    private ImageButton nextBtn;
    private ImageButton playBtn;
    private ImageButton btnPlayMode;
    private PlayerReceiver playerReceiver;
    private List<Mp3Info> mp3Infos;

    public PlayerPanelFragment() {
        // Required empty public constructor
    }

    public static PlayerPanelFragment newInstance() {
        Log.d(AppConstant.LOG.Com_Frag_Aty + "_TEST", "What happened here?");
        PlayerPanelFragment fragment = new PlayerPanelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(AppConstant.LOG.Fragment_oncreate, "create");
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.ACTION.CTL_ACTION);
        filter.addAction(AppConstant.ACTION.SET_SPOT_REFRESH);
        filter.addAction(AppConstant.ACTION.MODE_ACTION);
        filter.addAction(AppConstant.ACTION.MUSIC_LISTPOS);
        filter.addAction(AppConstant.ACTION.MUSIC_CURRENT);
        filter.addAction(AppConstant.ACTION.MUSIC_DURATION);
        filter.addAction(AppConstant.ACTION.MUSIC_CACHE);
        getActivity().registerReceiver(playerReceiver, filter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(AppConstant.LOG.Fragment_onview, "createView");
        View view = inflater.inflate(R.layout.fragment_player_panel, container, false);
        initView(view);
        setOnClickListener();
        return view;
    }

    private void initView(View view) {
        seekBar = (SeekBar) view.findViewById(R.id.seekbar_player);
        tv_Title = (TextView) view.findViewById(R.id.tv_titlePlayingMusic);
        previousBtn = (ImageButton) view.findViewById(R.id.btn_playerPreviousPiece);
        nextBtn = (ImageButton) view.findViewById(R.id.btn_playerNextPiece);
        playBtn = (ImageButton) view.findViewById(R.id.btn_playerPlayMusic);
        btnPlayMode = (ImageButton) view.findViewById(R.id.btn_playerPlayMode);
        mp3Infos = MusicListUtil.getMp3Infos(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WP_onRESUME", "onRsume_frag");
        //首先判断PlayerService是否已经启动
        //呼叫Service返回当前播放信息
        if (isServiceRunning(getActivity(), "edu.ecustcs123.zhh.walplay.PlayerService")) {
            Log.d("WP_Refreshing", "refreshing-------");
            Intent intent = new Intent(AppConstant.ACTION.GET_SPOT_REFRESH);
            getActivity().sendBroadcast(intent);//通过广播的方式通知PlayerService返回当前播放信息
        }
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        ActivityManager activityManager =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList =
                activityManager.getRunningServices(100);
        if (serviceInfoList.size() > 0) {
            //Log.d("WP_Test_serviceSize", String.valueOf(serviceInfoList.size()));
            for (int i = 0; i < serviceInfoList.size(); i++)                //Log.d("WP_Test_ClassName",serviceInfoList.get(i).service.getClassName());
            {
                if (serviceInfoList.get(i).service.getClassName().equals(className)) {

                    return true;
                }
            }
        }
        return false;
    }


    private void setOnClickListener() {
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        previousBtn.setOnClickListener(viewOnClickListener);
        nextBtn.setOnClickListener(viewOnClickListener);
        playBtn.setOnClickListener(viewOnClickListener);
        btnPlayMode.setOnClickListener(viewOnClickListener);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        tv_Title.setOnClickListener(viewOnClickListener);
    }

    /**
     * 按钮点击逻辑
     */
    private class ViewOnClickListener implements View.OnClickListener {
        Intent intent = new Intent();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_titlePlayingMusic:
                    //跳转详情界面
                    ((ViewPager)getActivity().findViewById(R.id.viewpager)).setCurrentItem(1);
                    break;
                case R.id.btn_playerPlayMode:
                    //更改播放模式
                    playingInfo.setPlayMode((playingInfo.getPlayMode() + 1) % 3);
                    intent.setAction(AppConstant.ACTION.CTL_ACTION);
                    intent.putExtra("playMode", playingInfo.getPlayMode());
                    getActivity().sendBroadcast(intent);//发送广播通知更改播放模式
                    switch (playingInfo.getPlayMode()) {
                        case AppConstant.PlayMode.MODE_RANDOM:
                            Toast.makeText(getActivity(), "随机播放", Toast.LENGTH_SHORT).show();
                            btnPlayMode.setBackgroundResource(R.drawable.glyphicons_random);
                            break;
                        case AppConstant.PlayMode.MODE_REPEAT:
                            Toast.makeText(getActivity(),"单曲循环",Toast.LENGTH_SHORT).show();
                            btnPlayMode.setBackgroundResource(R.drawable.glyphicons_roundabout);
                            break;
                        case AppConstant.PlayMode.MODE_ROUND:
                            Toast.makeText(getActivity(),"列表循环", Toast.LENGTH_SHORT).show();
                            btnPlayMode.setBackgroundResource(R.drawable.glyphicons_repeat);
                            break;
                    }
                    break;
                case R.id.btn_playerPreviousPiece://上一首
                    PreviousPiece();
                    break;
                case R.id.btn_playerNextPiece://下一首
                    NextPiece();
                    break;
                case R.id.btn_playerPlayMusic://播放|暂停
                    if (playingInfo.isPlaying()) {
                        //暂停
                        playBtn.setBackgroundResource(R.drawable.glyphicons_play);
                        playingInfo.setIsPlaying(false);
                        playingInfo.setIsPause(true);
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                        intent.setPackage("edu.ecustcs123.zhh.walplay");
                        getActivity().startService(intent);
                    } else {
                        if (playingInfo.isPause()) {
                            //恢复播放
                            playBtn.setBackgroundResource(R.drawable.glyphicons_pause);
                            playingInfo.setIsPlaying(true);
                            playingInfo.setIsPause(false);
                            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
                            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                            intent.setPackage("edu.ecustcs123.zhh.walplay");
                            getActivity().startService(intent);

                        } else {
                            //首次播放
                            playingInfo.setIsPlaying(true);
                            playingInfo.setIsPause(false);
                            if (mp3Infos.size() < 1) {
                                //没有可播放的
                                playingInfo.setIsPlaying(false);
                                playingInfo.setIsPause(false);
                                Toast.makeText(getActivity(), "当前列表没有音乐可播放", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            FirstPiece();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 播放下一首
     */
    public void NextPiece() {
        if (playingInfo.getListPos() < mp3Infos.size() - 1) {
            playingInfo.setListPos(playingInfo.getListPos() + 1);
            UpdatePlayingInfo(1);
            StartPlaying();
        } else {
            Toast.makeText(getActivity(), "当前列表已没有下一首可播放", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放上一首
     */
    public void PreviousPiece() {
        if (playingInfo.getListPos() > 0) {
            playingInfo.setListPos(playingInfo.getListPos() - 1);
            UpdatePlayingInfo(1);
            StartPlaying();
        } else {
            Toast.makeText(getActivity(), "当前列表已没有上一首可播放", Toast.LENGTH_SHORT).show();
        }
    }

    public void FirstPiece() {
        playingInfo.setListPos(0);
        UpdatePlayingInfo(1);
        StartPlaying();
    }

    /**
     * 已经确定好playingInfo.getListPos()，广播请求播放
     */
    private void StartPlaying() {
        Intent intent = new Intent();
        intent.putExtra("listPos", playingInfo.getListPos());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//这里好像应该换
        intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
        intent.setPackage("edu.ecustcs123.zhh.walplay");
        playingInfo.setIsPause(false);
        playingInfo.setIsPlaying(true);
        getActivity().startService(intent);
    }

    /**
     * 播放新的一首音乐，更新playingInfo和view
     *
     * @param type 更新方式，1：通过listPos更新
     */
    public void UpdatePlayingInfo(int type) {
        switch (type) {
            case 1:
                int pos = playingInfo.getListPos();
                playingInfo.setUrl(mp3Infos.get(pos).getUrl());
                playingInfo.setMusicTitle(mp3Infos.get(pos).getTitle());
                playingInfo.setDuration(mp3Infos.get(pos).getDuration());
                playingInfo.setCurrentTime(0);
                seekBar.setProgress(0);
                seekBar.setMax((int) playingInfo.getDuration());
                tv_Title.setText(playingInfo.getMusicTitle());
                break;
        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 拖动进度条后对service的数据交互
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                Intent intent = new Intent();
                intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                intent.putExtra("listPos", playingInfo.getListPos());
                intent.putExtra("progress", progress);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
                intent.setPackage("edu.ecustcs123.zhh.walplay");
                getActivity().startService(intent);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //----------与PlayerService的交互----------//
    public class PlayerReceiver extends BroadcastReceiver {
        /**
         * 接受PlayerService来的广播并进行playingInfo和seekBar的更新
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.d("WP_Receive----Action", action);
            if (action.equals(AppConstant.ACTION.SET_SPOT_REFRESH)) {
                //--------------------SET_SPOT_REFRESH
                //更新playingInfo
                playingInfo = intent.getParcelableExtra(AppConstant.KEY.PARCELABLE_PLAYINGINFO);
                //更新view
                Log.d("WP_onReceive", "-------------------");
                Log.d("WP_onReceive_Info", String.valueOf(playingInfo));
                tv_Title.setText(playingInfo.getMusicTitle());
                seekBar.setMax((int) playingInfo.getDuration());
                seekBar.setProgress((int) playingInfo.getCurrentTime());
            } else if (action.equals((AppConstant.ACTION.MUSIC_CURRENT))) {
                //--------------------MUSIC_CURRENT
                playingInfo.setCurrentTime(intent.getIntExtra("currentTime", -1));
//                Log.d(AppConstant.LOG.test151212, "-------getCurrentTime");
//                Log.d(AppConstant.LOG.test151212, String.valueOf(playingInfo.getCurrentTime()));
                seekBar.setProgress((int) playingInfo.getCurrentTime());
            } else if (action.equals(AppConstant.ACTION.MUSIC_DURATION)) {
                //--------------------MUSIC_DURATION
//                Log.d(AppConstant.LOG.test151212,"-------getDuration");
                playingInfo.setDuration(intent.getIntExtra("duration", 100));
                seekBar.setMax((int) playingInfo.getDuration());
            } else if (action.equals(AppConstant.ACTION.MUSIC_LISTPOS)) {
                //--------------------MUSIC_LISTPOS
                playingInfo.setListPos(intent.getIntExtra("listPos", -1));
                UpdatePlayingInfo(1);
            } else if(action.equals(AppConstant.ACTION.MUSIC_CACHE)){
                //--------------------MUSIC_CACHE    update secondaryProgress
                float percent = intent.getFloatExtra("percent",0);
                Log.d(AppConstant.LOG.WPDEBUG+"___cached", String.valueOf(percent));
                int t =(int) (percent * (float) seekBar.getMax());
                Log.d(AppConstant.LOG.WPDEBUG+"___tttt", String.valueOf(t));
                seekBar.setSecondaryProgress((int) (percent * (float) seekBar.getMax()));
            } else if(action.equals(AppConstant.ACTION.MUSIC_TITLE)){
                String title = intent.getStringExtra("title");
                tv_Title.setText(title);
                playingInfo.setMusicTitle(title);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(playerReceiver);
    }
}
