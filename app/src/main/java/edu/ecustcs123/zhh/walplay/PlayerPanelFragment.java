package edu.ecustcs123.zhh.walplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerPanelFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerPanelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerPanelFragment extends Fragment {

    private PlayingInfo playingInfo = new PlayingInfo();

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
    // TODO: Rename and change types and number of parameters
    public static PlayerPanelFragment newInstance(String param1, String param2) {
        Log.d(AppConstant.LOG.Com_Frag_Aty+"_TEST","What happened here?");
        PlayerPanelFragment fragment = new PlayerPanelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = new Bundle();
        b = getArguments();
        if(b != null){
            playingInfo = b.getParcelable(AppConstant.KEY.PARCELABLE_PLAYINGINFO);
            Log.d(AppConstant.LOG.Test_PlayingInfo+"title",playingInfo.getMusicTitle());
            Log.d(AppConstant.LOG.Test_PlayingInfo+"pos", String.valueOf(playingInfo.getListPos()));
            Log.d(AppConstant.LOG.Test_PlayingInfo+"current", String.valueOf(playingInfo.getCurrentTime()));
            Log.d(AppConstant.LOG.Test_PlayingInfo+"dura", String.valueOf(playingInfo.getDuration()));
        }

        Log.d(AppConstant.LOG.Fragment_oncreate,"create");
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppConstant.ACTION.CTL_ACTION);
        filter.addAction(AppConstant.ACTION.MODE_ACTION);
        filter.addAction(AppConstant.ACTION.UPDATE_ACTION);
        filter.addAction(AppConstant.ACTION.MUSIC_CURRENT);
        filter.addAction(AppConstant.ACTION.MUSIC_DURATION);
        getActivity().registerReceiver(playerReceiver, filter);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(AppConstant.LOG.Fragment_onview, "createView");
        View view = inflater.inflate(R.layout.fragment_player_panel, container, false);
        init(view);
        setOnClickListener();
        return view;
    }

    private void init(View view){
        seekBar = (SeekBar) view.findViewById(R.id.seekbar_player);
        tv_Title = (TextView) view.findViewById(R.id.tv_titlePlayingMusic);
        previousBtn = (ImageButton) view.findViewById(R.id.btn_playerPreviousPiece);
        nextBtn = (ImageButton) view.findViewById(R.id.btn_playerNextPiece);
        playBtn = (ImageButton) view.findViewById(R.id.btn_playerPlayMusic);
        btnPlayMode = (ImageButton) view.findViewById(R.id.btn_playerPlayMode);

        mp3Infos = MusicListUtil.getMp3Infos(getActivity());

        tv_Title.setText(playingInfo.getMusicTitle());
        seekBar.setMax((int) playingInfo.getDuration());
        seekBar.setProgress((int) playingInfo.getCurrentTime());
    }

    private void setOnClickListener(){
        ViewOnClickListener viewOnClickListener = new ViewOnClickListener();
        previousBtn.setOnClickListener(viewOnClickListener);
        nextBtn.setOnClickListener(viewOnClickListener);
        playBtn.setOnClickListener(viewOnClickListener);
        btnPlayMode.setOnClickListener(viewOnClickListener);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
    }
    /**
     * 按钮点击逻辑
     */
    private class ViewOnClickListener implements View.OnClickListener{
        Intent intent = new Intent();
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_playerPlayMode:
                    //更改播放模式
                    playingInfo.setPlayMode((playingInfo.getPlayMode()+1) % 3);
                    intent.setAction(AppConstant.ACTION.CTL_ACTION);
                    intent.putExtra("playMode",playingInfo.getPlayMode());
                    getActivity().sendBroadcast(intent);//发送广播通知更改播放模式
                    switch (playingInfo.getPlayMode()){
                        case AppConstant.PlayMode.MODE_RANDOM:
                            Toast.makeText(getActivity(), "随机播放", Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstant.PlayMode.MODE_REPEAT:
                            Toast.makeText(getActivity(),"单曲循环",Toast.LENGTH_SHORT).show();
                            break;
                        case AppConstant.PlayMode.MODE_ROUND:
                            Toast.makeText(getActivity(),"列表循环", Toast.LENGTH_SHORT).show();
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
                    if(playingInfo.isPlaying()) {
                        //暂停
                        playingInfo.setIsPlaying(false);
                        playingInfo.setIsPause(true);
                        intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
                        intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                        intent.setPackage("edu.ecustcs123.zhh.walplay");
                        getActivity().startService(intent);
                    }else{
                        if(playingInfo.isPause()){
                            //恢复播放
                            playingInfo.setIsPlaying(true);
                            playingInfo.setIsPause(false);
                            intent.putExtra("MSG",AppConstant.PlayerMsg.CONTINUE_MSG);
                            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                            intent.setPackage("edu.ecustcs123.zhh.walplay");
                            getActivity().startService(intent);

                        }else{
                            //首次播放
                            playingInfo.setIsPlaying(true);
                            playingInfo.setIsPause(false);
                            if(mp3Infos.size()<1){
                                //没有可播放的
                                playingInfo.setIsPlaying(false);
                                playingInfo.setIsPause(false);
                                Toast.makeText(getActivity(),"当前列表没有音乐可播放", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            StartPlaying(0);
                        }
                    }
                    break;
            }
        }
    }
    /**
     * 播放下一首
     */
    public void NextPiece(){
        if(playingInfo.getListPos() < mp3Infos.size()-1){
            playingInfo.setListPos(playingInfo.getListPos()+1);
            UpdatePlayingInfo(1);
            //还是通过startService(intent)来启动播放
            Mp3Info mp3Info = mp3Infos.get(playingInfo.getListPos());
            tv_Title.setText(mp3Info.getTitle());//改标题
            Intent intent = new Intent();
            intent.putExtra("listPos",playingInfo.getListPos());
            intent.putExtra("url",mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//这里好像应该换
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            getActivity().startService(intent);
        }else{
            Toast.makeText(getActivity(),"当前列表已没有下一首可播放",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 播放上一首
     */
    public void PreviousPiece(){
        if(playingInfo.getListPos()>0){
            playingInfo.setListPos(playingInfo.getListPos()-1);
            UpdatePlayingInfo(1);
            Mp3Info mp3Info = mp3Infos.get(playingInfo.getListPos());
            tv_Title.setText(mp3Info.getTitle());//改标题
            Intent intent = new Intent();
            intent.putExtra("listPos",playingInfo.getListPos());
            intent.putExtra("url", mp3Info.getUrl());
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);//这里好像应该换
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            getActivity().startService(intent);
        }else{

            Toast.makeText(getActivity(), "当前列表已没有上一首可播放", Toast.LENGTH_SHORT).show();
        }
    }

    public void StartPlaying(int listPos){
        playingInfo.setListPos(listPos);
        Mp3Info mp3Info = mp3Infos.get(playingInfo.getListPos());
        tv_Title.setText(mp3Info.getTitle());
        playingInfo.setUrl(mp3Info.getUrl());
        Intent intent = new Intent();
        intent.putExtra("listPos",playingInfo.getListPos());
        intent.putExtra("url", playingInfo.getUrl());
        intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
        intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
        intent.setPackage("edu.ecustcs123.zhh.walplay");
        playingInfo.setIsPause(false);
        playingInfo.setIsPlaying(true);
        getActivity().startService(intent);
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        /**
         * 拖动进度条后对service的数据交互
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                Intent intent = new Intent();
                intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
                intent.putExtra("listPos", playingInfo.getListPos());
                intent.putExtra("url", playingInfo.getUrl());
                intent.putExtra("progress", progress);
                intent.putExtra("MSG", AppConstant.PlayerMsg.PROGRESS_CHANGE);
                intent.setPackage("edu.ecustcs123.zhh.walplay");
//                Log.d(AppConstant.LOG.LOG_PROGRESSBAR_CHANGED, String.valueOf(progress));
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

    public void UpdatePlayingInfo(int type){
        switch (type){
            case 1:
                int pos = playingInfo.getListPos();
                playingInfo.setUrl(mp3Infos.get(pos).getUrl());
                playingInfo.setMusicTitle(mp3Infos.get(pos).getTitle());
                playingInfo.setDuration(mp3Infos.get(pos).getDuration());
                playingInfo.setCurrentTime(0);
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PlayerReceiver extends BroadcastReceiver {
        /**
         * 接受PlayerService来的广播并进行playingInfo和seekBar的更新
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(AppConstant.ACTION.MODE_ACTION)){
                //TODO:what would happen here?
            }else if(action.equals((AppConstant.ACTION.MUSIC_CURRENT))){
                playingInfo.setCurrentTime(intent.getIntExtra("currentTime",-1));
                seekBar.setProgress((int)playingInfo.getCurrentTime());
            }else if(action.equals(AppConstant.ACTION.MUSIC_DURATION)){
                playingInfo.setDuration(intent.getIntExtra("duration",-1));
                seekBar.setMax((int)playingInfo.getDuration());
            }else if(action.equals(AppConstant.ACTION.UPDATE_ACTION)){
                playingInfo.setCurrentTime(0);
                playingInfo.setListPos(intent.getIntExtra("listPos", -1));
                playingInfo.setUrl(mp3Infos.get(playingInfo.getListPos()).getUrl());
                playingInfo.setDuration(mp3Infos.get(playingInfo.getListPos()).getDuration());
                tv_Title.setText(mp3Infos.get(playingInfo.getListPos()).getTitle());
                seekBar.setProgress(0);
            }
        }
    }

    public void getPlayingInfo(CallBack callback){
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstant.KEY.PARCELABLE_PLAYINGINFO, playingInfo);
//        Log.d(AppConstant.LOG.Com_Frag_Aty, "setBundle_Frag");
        callback.getResult(bundle);
    }
    //自定义接口，在activity回调时重载getResult方法得到result值
    public interface CallBack{
        public void getResult(Bundle bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(playerReceiver);
    }
}
