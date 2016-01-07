package edu.ecustcs123.zhh.walplay;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import java.util.List;
import java.util.zip.Inflater;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.AppStatus;
import edu.ecustcs123.zhh.walplay.Utils.Mp3Info;
import edu.ecustcs123.zhh.walplay.Utils.MusicListUtil;
import edu.ecustcs123.zhh.walplay.Utils.PagerAdapter;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private String[] drawerTitles;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private List<Mp3Info> mp3Infos;
    private ViewPager viewPager;
    private PlayerPanelFragment playerPanelFragment;
    private boolean bPlayerPanelFragment =false;
    private ListFragment.LBSReceiver lbsReceiver;

    private AppStatus appStatus;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }

    @Override
    protected void onPause(){
        super.onPause();
        if (playerPanelFragment.playingInfo.isPlaying()) {
            //暂停
            Intent intent=new Intent();
            findViewById(R.id.btn_playerPlayMusic).setBackgroundResource(R.drawable.glyphicons_play);
            playerPanelFragment.playingInfo.setIsPlaying(false);
            playerPanelFragment.playingInfo.setIsPause(true);
            intent.putExtra("MSG", AppConstant.PlayerMsg.PAUSE_MSG);
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            startService(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (playerPanelFragment.playingInfo.isPause()) {
            //恢复播放
            Intent intent = new Intent();
            findViewById(R.id.btn_playerPlayMusic).setBackgroundResource(R.drawable.glyphicons_pause);
            playerPanelFragment. playingInfo.setIsPlaying(true);
            playerPanelFragment.playingInfo.setIsPause(false);
            intent.putExtra("MSG", AppConstant.PlayerMsg.CONTINUE_MSG);
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            startService(intent);

        } else {
            //首次播放
            playerPanelFragment.playingInfo.setIsPlaying(true);
            playerPanelFragment.playingInfo.setIsPause(false);
            mp3Infos= MusicListUtil.getMp3Infos(this);
            if (mp3Infos.size() < 1) {
                //没有可播放的
                playerPanelFragment. playingInfo.setIsPlaying(false);
                playerPanelFragment.playingInfo.setIsPause(false);
                Toast.makeText(this, "当前列表没有音乐可播放", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //界面下方始终有不变的playerPanelFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!bPlayerPanelFragment){
            playerPanelFragment = new PlayerPanelFragment();
            transaction.add(R.id.fragmentContainer_controlPanel,playerPanelFragment);
            transaction.commit();
            bPlayerPanelFragment = true;
        }

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(false);

        //viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //处理DrawerLayout相关
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                Toast.makeText(getApplicationContext(), String.valueOf(item.getItemId()), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        appStatus =(AppStatus) getApplicationContext();
        init();
    }

    private void init(){
//        View headerView = navigationView.findViewById()
        int a = navigationView.getHeaderCount();
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.nav_tv_userName);

        nav_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppConstant.LOG.WPDLOGIN,"nav_user clicked");

                if(false){//已经登陆，跳转到个人信息aty

                }else{//尚未登陆，跳转到register.aty
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SelectDrawerItem(position);
        }
    }

    private void SelectDrawerItem(int position){
        //TODO index to next page
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListFragment(), "列表");
        adapter.addFragment(new InfoFragment(), "详情");
        adapter.addFragment(new MapFragment(), "地图");
        viewPager.setAdapter(adapter);
    }

}

