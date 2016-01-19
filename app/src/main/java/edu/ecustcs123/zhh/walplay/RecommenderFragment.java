package edu.ecustcs123.zhh.walplay;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ecustcs123.zhh.walplay.Utils.WalAdapter;

public class RecommenderFragment extends Fragment {

    public static RecommenderFragment newInstance(String param1, String param2) {
        RecommenderFragment fragment = new RecommenderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recommender, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewpager_recommend);
        if(viewPager != null){
            setupViewPager(viewPager);
        }

        TabLayout tabs = (TabLayout) (v.findViewById(R.id.tabs_recommend));
        if(tabs != null){
            tabs.setupWithViewPager(viewPager);
        }


        return v;
    }

    private void setupViewPager(ViewPager viewPager) {
        WalAdapter adapter = new WalAdapter(getFragmentManager());

        adapter.addFragment(new ListFragment(), "testing");
        adapter.addFragment(new ListFragment(),"caaaaa");
        viewPager.setAdapter(adapter);
    }
}
