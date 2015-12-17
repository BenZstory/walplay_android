package edu.ecustcs123.zhh.walplay;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
/**
 * Created by BenZ on 2015/12/8.
 * zhengbin0320@gmail.com
 */
public class ListActivity extends AppCompatActivity {
    //List Panel
    private ListView mMusicListView;//列表view
    private List<Mp3Info> mp3Infos;//保存mp3info的列表
    private SimpleAdapter mAdapter;//列表adapter
    private PlayerPanelFragment playerPanelFragment;
    private LBSReceiver lbsReceiver;

    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_spotInfo;
    private Button btn_startSpotPlay;
    private Button btn_startLoc;
    private TextView tv_errCode;

    @Override
    protected void onResume() {
        super.onResume();
    }

//    private Button btnRemoteTest;

    //-----ON CREATE-----
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mMusicListView = (ListView) findViewById(R.id.lv_musiclistview);
        mMusicListView.setOnItemClickListener(new MusicItemOnClickListener());//注册click监听器，自定义实现listener
        mp3Infos = MusicListUtil.getMp3Infos(this);//从数据库获取歌曲列表
        setListAdapter(MusicListUtil.getMusicMaps(mp3Infos));//bind mp3 list
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        playerPanelFragment = new PlayerPanelFragment();
        transaction.add(R.id.fragmentContainer_controlPanel, playerPanelFragment);
        transaction.commit();


        /*//TODO 下载remoteMusic
        btnRemoteTest = (Button) findViewById(R.id.btn_remoteTest);
        btnRemoteTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppConstant.ACTION.SPOT_SERVICE);
                intent.putExtra("spotId",-1);
                intent.putExtra("downloadUrl",new String("http://222.186.30.212:8082/demo_store/2015/12/13/18130acf26be437ceb9f9e63b5479624.mp3"));
                intent.setPackage("edu.ecustcs123.zhh.walplay");
                startService(intent);
            }
        });*/

        //TODO 显示loc latlng
        tv_latitude = (TextView) findViewById(R.id.tv_lat);
        tv_longitude = (TextView) findViewById(R.id.tv_lng);
        tv_spotInfo = (TextView) findViewById(R.id.tv_spotInfo);
        btn_startSpotPlay = (Button) findViewById(R.id.btn_startSpotPlay);
        btn_startLoc = (Button) findViewById(R.id.btn_startLoc);
        tv_errCode = (TextView) findViewById(R.id.tv_errCode);

        btn_startLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startLoc = new Intent(AppConstant.ACTION.START_LBS_SERVICE);
                startLoc.setPackage("edu.ecustcs123.zhh.walplay");
                startService(startLoc);
            }
        });


        //注册lbsReceiver，处理每次返回的lbs信息
        lbsReceiver = new LBSReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.ACTION.GET_LOC);
        intentFilter.addAction(AppConstant.ACTION.NOTIFY_SPOT);
        this.registerReceiver(lbsReceiver,intentFilter);

        Intent intent = new Intent(AppConstant.ACTION.START_LBS_SERVICE);
        intent.setPackage("edu.ecustcs123.zhh.walplay");
        Log.d(AppConstant.LOG.WPLBSDEBUG,"Starting LBSService......");
        startService(intent);
    }

    /** 
     * Get clicked pos and Start the PlayerService to play the musicpiece
     */
    private class MusicItemOnClickListener implements AdapterView.OnItemClickListener{
        //TODO:不仅只是播放，还应有别的按钮，如何使item部分被点击
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO:SpotService(pos)->PlayerService(pos)

            //把新spot事件交给SpotService处理
            Intent intent = new Intent(AppConstant.ACTION.SPOT_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            intent.putExtra("spotId",position);
            startService(intent);

            /*Log.d("WP_Clicked------", String.valueOf(position));
            Intent intent  = new Intent();
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            intent.putExtra("listPos", position);
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            startService(intent);*/
            final Intent intent2 = new Intent();
            intent2.setClass(ListActivity.this, SpotDetailActivity.class);
            intent2.putExtra("listPos", position);
            startActivity(intent2);
//            Log.d("startPlaying-->", String.valueOf(position));
        }
    }

    public void setListAdapter(List<HashMap<String,Object>> list) {
        //TODO:ibtn会抢占焦点，先把btn去掉，以后再做决断
        mAdapter = new SimpleAdapter(this,list,R.layout.music_item,
                new String[] {"title","artist","duration"},
                new int[] {R.id.tv_itemMusicName,R.id.tv_itemMusicInfo,R.id.tv_itemMusicDuration}
        );
        mMusicListView.setAdapter(mAdapter);
    };

    public class LBSReceiver extends BroadcastReceiver{
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
                StringBuffer sb = new StringBuffer(256);
                tv_spotInfo.setText(getString(R.string.inside1)+String.valueOf(spotId)+getString(R.string.inside2));
                btn_startSpotPlay.setClickable(true);
            }
        }
    }


}
