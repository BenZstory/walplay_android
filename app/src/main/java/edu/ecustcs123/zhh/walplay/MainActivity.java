package edu.ecustcs123.zhh.walplay;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends android.support.v7.app.ActionBarActivity implements android.support.v7.app.ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private List<ActionBarTab> tabsList = new ArrayList<ActionBarTab>(2);
    private ViewPager viewPager;
    private android.support.v7.app.ActionBar actionBar;
    private PlayerPanelFragment playerPanelFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加载各个Fragment
        tabsList.add(new ActionBarTab("列表",ListFragment.class));
        tabsList.add(new ActionBarTab("详情",InfoFragment.class));

        FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        playerPanelFragment = new PlayerPanelFragment();
        transaction.add(R.id.fragmentContainer_controlPanel,playerPanelFragment);
        transaction.commit();
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
