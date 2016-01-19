package edu.ecustcs123.zhh.walplay;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ecustcs123.zhh.walplay.Utils.WalAdapter;

public class InfoFragment extends Fragment {

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_info, container, false);

        ViewPager viewPager = (ViewPager) v.findViewById(R.id.info_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);

            TabLayout tabLayout = (TabLayout) v.findViewById(R.id.info_tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

        return v;
    }

    private void setupViewPager(ViewPager viewPager){
        WalAdapter adapter = new WalAdapter(getFragmentManager());
    }

}
