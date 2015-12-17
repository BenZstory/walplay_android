package edu.ecustcs123.zhh.walplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ListFragment extends Fragment {
    //List Panel
    private ListView mMusicListView;//列表view
    private List<Mp3Info> mp3Infos;//保存mp3info的列表
    private SimpleAdapter mAdapter;//列表adapter
    private PlayerPanelFragment playerPanelFragment;
    private LBSReceiver lbsReceiver;

    //LBS test
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_errCode;
    private TextView tv_spotInfo;
    private Button btn_startSpotPlay;
    private Button btn_startLoc;

    private Vibrator vibrator;
    
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){
        Log.d(AppConstant.LOG.Fragment_onview, "createView");
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initView(view);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        return view;
    }

    private void initView(View view){
        mMusicListView = (ListView) view.findViewById(R.id.lv_musiclistview);
        mMusicListView.setOnItemClickListener(new MusicItemOnClickListener());//注册click监听器，自定义实现listener
        mp3Infos = MusicListUtil.getMp3Infos(getActivity());//从数据库获取歌曲列表
        setListAdapter(MusicListUtil.getMusicMaps(mp3Infos));//bind mp3 list

        tv_latitude = (TextView) view.findViewById(R.id.tv_lat);
        tv_longitude = (TextView) view.findViewById(R.id.tv_lng);
        tv_errCode = (TextView) view.findViewById(R.id.tv_errCode);
        tv_spotInfo = (TextView) view.findViewById(R.id.tv_spotInfo);
        btn_startSpotPlay = (Button) view.findViewById(R.id.btn_startSpotPlay);
        btn_startLoc = (Button) view.findViewById(R.id.btn_startLoc);

        btn_startLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeSpot = new Intent(AppConstant.ACTION.CHANGE_SPOT_STATUS);
                getActivity().sendBroadcast(changeSpot);

//                Intent startLoc = new Intent(AppConstant.ACTION.START_LBS_SERVICE);
//                startLoc.setPackage("edu.ecustcs123.zhh.walplay");
//                getActivity().startService(startLoc);
            }
        });

        //默认spotPlay不可点击
        btn_startSpotPlay.setClickable(false);
        //TODO 测试接受推送，直接online播放mp3
        btn_startSpotPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppConstant.ACTION.SPOT_SERVICE);
                intent.putExtra("spotId",-1);
                intent.putExtra("downloadUrl",new String("http://222.186.30.212:8082/demo_store/2015/12/13/18130acf26be437ceb9f9e63b5479624.mp3"));
                intent.setPackage("edu.ecustcs123.zhh.walplay");
                Log.d(AppConstant.LOG.WPSPOT,"start new spot service");
                getActivity().startService(intent);
            }
        });

        //注册lbsReceiver，处理每次返回的lbs信息
        lbsReceiver = new LBSReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.ACTION.GET_LOC);
        intentFilter.addAction(AppConstant.ACTION.NOTIFY_SPOT);
        getActivity().registerReceiver(lbsReceiver,intentFilter);

        Intent intent = new Intent(AppConstant.ACTION.START_LBS_SERVICE);
        intent.setPackage("edu.ecustcs123.zhh.walplay");
        Log.d(AppConstant.LOG.WPLBSDEBUG,"Starting LBSService......");
        getActivity().startService(intent);
    }

    //-----ON CREATE-----
    protected void onCreate(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Get clicked pos and Start the PlayerService to play the musicpiece
     */
    private class MusicItemOnClickListener implements AdapterView.OnItemClickListener{
        //TODO:不仅只是播放，还应有别的按钮，如何使item部分被点击
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("WP_Clicked------", String.valueOf(position));
            Intent intent  = new Intent();
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            intent.putExtra("listPos", position);
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            getActivity().startService(intent);

            //跳转详情界面
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer_controlPanel,new PlayerPanelFragment()).addToBackStack(null).commit();
            getFragmentManager().beginTransaction().replace(R.id.Info_Fragment,new InfoFragment()).addToBackStack(null).commit();
            ((ViewPager)getActivity().findViewById(R.id.MainViewPager)).setCurrentItem(1);

            /*getFragmentManager().beginTransaction().replace(R.id.List_Fragment,new InfoFragment()).addToBackStack(null).commit();*/
        }
    }

    public void setListAdapter(List<HashMap<String,Object>> list) {
        //TODO:ibtn会抢占焦点，先把btn去掉，以后再做决断
        mAdapter = new SimpleAdapter(getActivity(),list,R.layout.music_item,
                new String[] {"title","artist","duration"},
                new int[] {R.id.tv_itemMusicName,R.id.tv_itemMusicInfo,R.id.tv_itemMusicDuration}
        );
        mMusicListView.setAdapter(mAdapter);
    }

    public class LBSReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(AppConstant.ACTION.GET_LOC)){
                Log.d(AppConstant.LOG.WPLBSDEBUG,"Get LBS return!");
                tv_latitude.setText(String.valueOf(intent.getDoubleExtra("latitude",0.0)));
                tv_longitude.setText(String.valueOf(intent.getDoubleExtra("longitude",0.0)));
                tv_errCode.setText(String.valueOf(intent.getIntExtra("locTyep",-1)));
            }else if(action.equals(AppConstant.ACTION.NOTIFY_SPOT)){
                int spotId = intent.getIntExtra("locArea",-1);
                Log.d(AppConstant.LOG.WPLBSDEBUG+"_receiveSpot", String.valueOf(spotId));
                if(spotId == -1){
                    //走出景区
                    tv_spotInfo.setText(getString(R.string.outside));
                    btn_startSpotPlay.setClickable(false);
                    vibrator.cancel();
                }else{
                    //进入景区spotId
                    long[] pattern = {400,400,400,400,400,400};
                    vibrator.vibrate(pattern, -1);
                    StringBuffer sb = new StringBuffer(256);
                    tv_spotInfo.setText(getString(R.string.inside1)+String.valueOf(spotId)+getString(R.string.inside2));
                    btn_startSpotPlay.setClickable(true);
                }
            }
        }
    }
}
