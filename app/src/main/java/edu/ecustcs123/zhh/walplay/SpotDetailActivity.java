package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpotDetailActivity extends FragmentActivity {
    private List<Mp3Info> mp3Infos;
    private SeekBar seekBar;
    private TextView tv_Title;
    private PlayingInfo playingInfo;

    private List<View> listviews;
    private Context context;
    private TabHost mTabHost;
    private LocalActivityManager manager;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);
        context=SpotDetailActivity.this;
        pager=(ViewPager) findViewById(R.id.viewpager);

        Intent intent = getIntent();
        int tmp = intent.getIntExtra("listPos", -1);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PlayerPanelFragment playerPanelFragment = new PlayerPanelFragment();

        if(tmp != -1){
            //通过点击item进入detail,需要重置listPos
            Log.d(AppConstant.LOG.NewSpotPlaying+"_ATY", String.valueOf(tmp));
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.KEY.NEW_PLAYING,tmp);
            playerPanelFragment.setArguments(bundle);
        }else{
            Bundle bundle = intent.getBundleExtra(AppConstant.KEY.PARCELABLE_PLAYINGINFO);
            Log.d(AppConstant.LOG.Com_Frag_Aty+"INTENT", String.valueOf(intent));
            playerPanelFragment.setArguments(bundle);           //获得playingInfo
            Log.d(AppConstant.LOG.Com_Frag_Aty+"_SAVING", String.valueOf(bundle));
            Log.d(AppConstant.LOG.Com_Frag_Aty, "putBundle_spotAty");
        }

        transaction.add(R.id.fragmentContainer_controlPanel,playerPanelFragment);
        transaction.commit();

        //定义一个放view的list，用于存放ViewPager用到的View
        listviews = new ArrayList<View>();

        manager = new LocalActivityManager(this,true);
        manager.dispatchCreate(savedInstanceState);

        Intent t1 = new Intent(context,T1Activity.class);
        listviews.add(getView("tab_1",t1));
        Intent t2 = new Intent(context,T2Activity.class);
        listviews.add(getView("tab_2",t2));
        Intent t3 = new Intent(context,T3Activity.class);
        listviews.add(getView("tab_3",t3));

        mTabHost=(TabHost)findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabHost.setup(manager);

        //定义TanHost 中的Tab
        RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget, null);
        TextView tvTab1 = (TextView)tabIndicator1.findViewById(R.id.tab_title);
        tvTab1.setText("图片");

        RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);
        TextView tvTab2 = (TextView)tabIndicator2.findViewById(R.id.tab_title);
        tvTab2.setText("评论");

        RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tabwidget,null);
        TextView tvTab3 = (TextView)tabIndicator3.findViewById(R.id.tab_title);
        tvTab3.setText("更多");

        intent = new Intent(context,EmptyActivity.class);
        //注意这儿Intent中的activity不能是自身 比如“tab_1”对应的是T1Activity，后面intent就new的T3Activity的。
        mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator(tabIndicator1).setContent(intent));
        mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator(tabIndicator2).setContent(intent));
        mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator(tabIndicator3).setContent(intent));


        pager .setAdapter(new MyPageAdapter(listviews));

        pager .setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //当viewPager发生改变时，同时改变tabhost上面的currentTab
                mTabHost.setCurrentTab(position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });


        //点击tabhost中的tab时，要切换下面的viewPager
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if ("tab_1".equals(tabId)) {
                    pager.setCurrentItem(0);
                }
                if ("tab_2".equals(tabId)) {

                    pager.setCurrentItem(1);
                }
                if ("tab_3".equals(tabId)) {
                    pager.setCurrentItem(2);
                }
            }
        });




    }
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
    private class MyPageAdapter extends PagerAdapter {

        private List<View> list;

        private MyPageAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object instantiateItem(View view, int position) {
            ViewPager pViewPager = ((ViewPager) view);
            pViewPager.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
}