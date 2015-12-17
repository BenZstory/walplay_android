package edu.ecustcs123.zhh.walplay;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {


    private List<View> listviews;
    private Context context;
    private TabHost mTabHost;
    private LocalActivityManager manager;
    private ViewPager pager;
    private View view;


    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(AppConstant.LOG.Fragment_onview, "createView");
        view = inflater.inflate(R.layout.fragment_info, container, false);
        initView(view);
        Intent intent = getActivity().getIntent();


        //定义一个放view的list，用于存放ViewPager用到的View
        listviews = new ArrayList<View>();

        manager = new LocalActivityManager(getActivity(), true);
        manager.dispatchCreate(savedInstanceState);

        Intent t1 = new Intent(context, T1Activity.class);
        listviews.add(getView("tab_1", t1));
        Intent t2 = new Intent(context, T2Activity.class);
        listviews.add(getView("tab_2", t2));
        Intent t3 = new Intent(context, T3Activity.class);
        listviews.add(getView("tab_3", t3));

        mTabHost = (TabHost) view.findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabHost.setup(manager);

        //定义TanHost 中的Tab
        RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tabwidget, null);
        TextView tvTab1 = (TextView) tabIndicator1.findViewById(R.id.tab_title);
        tvTab1.setText("图片");

        RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tabwidget, null);
        TextView tvTab2 = (TextView) tabIndicator2.findViewById(R.id.tab_title);
        tvTab2.setText("评论");

        RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tabwidget, null);
        TextView tvTab3 = (TextView) tabIndicator3.findViewById(R.id.tab_title);
        tvTab3.setText("更多");

        intent = new Intent(context, EmptyActivity.class);
        //注意这儿Intent中的activity不能是自身 比如“tab_1”对应的是T1Activity，后面intent就new的T3Activity的。
        mTabHost.addTab(mTabHost.newTabSpec("tab_1").setIndicator(tabIndicator1).setContent(intent));
        mTabHost.addTab(mTabHost.newTabSpec("tab_2").setIndicator(tabIndicator2).setContent(intent));
        mTabHost.addTab(mTabHost.newTabSpec("tab_3").setIndicator(tabIndicator3).setContent(intent));


        pager.setAdapter(new MyPageAdapter(listviews));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        return view;
    }

    private void initView(View view) {
        context = getActivity();
        pager = (ViewPager) view.findViewById(R.id.viewpager);


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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







