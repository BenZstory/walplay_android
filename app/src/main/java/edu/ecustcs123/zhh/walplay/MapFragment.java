package edu.ecustcs123.zhh.walplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class MapFragment extends Fragment {
    private MapView mapView;
    private BaiduMap baiduMap;
    private TextView tv_lat;
    private TextView tv_lng;
    private boolean isFirstLoc = true;
    private Button btnSaveLoc;
    private LBSReceiver lbsReceiver;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mapView = (MapView) view.findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        btnSaveLoc = (Button) view.findViewById(R.id.btn_saveLoc);
        lbsReceiver = new LBSReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.ACTION.GET_LOC);
        getActivity().registerReceiver(lbsReceiver, intentFilter);

        btnSaveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save loc to preference->remote

            }
        });
    }

    public class LBSReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals((AppConstant.ACTION.GET_LOC))){
                double lat = intent.getDoubleExtra("latitude", 0.0);
                double lng = intent.getDoubleExtra("longitude", 0.0);
                double radius = intent.getDoubleExtra("radius", 0.0);
                int locType = intent.getIntExtra("locType",-1);
                LatLng ll = new LatLng(lat,lng);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
                baiduMap.animateMapStatus(u);
                Log.d(AppConstant.LOG.WPLBSDEBUG + "map", "updated");
            }
        }
    }
}
