package edu.ecustcs123.zhh.walplay;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Destroyable;

public class MainActivity extends android.support.v7.app.ActionBarActivity implements android.support.v7.app.ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private List<Mp3Info> mp3Infos;
    private List<ActionBarTab> tabsList = new ArrayList<ActionBarTab>(2);
    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;
    private PlayerPanelFragment playerPanelFragment;
    private boolean bPlayerPanelFragment =false;
    private ListFragment.LBSReceiver lbsReceiver ;



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
            mp3Infos=MusicListUtil.getMp3Infos(this);
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



        //加载各个Fragment
        tabsList.add(new ActionBarTab("列表",ListFragment.class));
        tabsList.add(new ActionBarTab("详情",InfoFragment.class));

        FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!bPlayerPanelFragment){
            playerPanelFragment = new PlayerPanelFragment();
            transaction.add(R.id.fragmentContainer_controlPanel,playerPanelFragment);
            transaction.commit();
            bPlayerPanelFragment = true;
        }

        initActionBar();


    }
    private void initActionBar(){

        viewPager = (ViewPager) this.findViewById(R.id.MainViewPager);
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);
        //去除默认ActionBar
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);


        //循环遍历 初始化Actionbar中的Tab
        for(ActionBarTab tab:tabsList){
            android.support.v7.app.ActionBar.Tab T = actionBar.newTab();
            T.setText(tab.getText());
            T.setTabListener(this);
            actionBar.addTab(T);
        }
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        actionBar.selectTab(actionBar.getTabAt(position));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }




    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }


    /**
     *ActionBar中的Tab类
     */
    class ActionBarTab {
        private String text; // 标题的文字
        private Class fragment;// 每一个tab所对应的页面fragment

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Class getFragment() {
            return fragment;
        }

        public void setFragment(Class fragment) {
            this.fragment = fragment;
        }

        /**
         * 两个参数的构造方法，便于创建一个对象
         */
        public ActionBarTab(String string, Class fragment) {
            this.text = string;
            this.fragment = fragment;
        }
    }
    /**
     * 为viewpager设置的适配器
     */
    class TabFragmentPagerAdapter extends FragmentPagerAdapter{

        public TabFragmentPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            try {
                return (Fragment) tabsList.get(position).getFragment().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;

        }



        @Override
        public int getCount() {
            return tabsList.size();
        }
    }
}
