package edu.ecustcs123.zhh.walplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MapActivity extends AppCompatActivity {
    private LocationClient locationClient = null;
    public MyLocationListener myLocationListener = new MyLocationListener();
    public LocationClientOption.LocationMode locationMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private MapView mapView;
    private BaiduMap baiduMap;
    private TextView tv_lat;
    private TextView tv_lng;
    private boolean isFirstLoc = true;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        mapView = (MapView)findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myLocationListener);

        tv_lat = (TextView) findViewById(R.id.tv_mapLat);
        tv_lng = (TextView) findViewById(R.id.tv_mapLng);
        btnStart = (Button) findViewById(R.id.btn_startLoc);
        Log.d(AppConstant.LOG.WPMAPDEBUG+"-----------","starting locationClient--");
        initOption();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClient.start();
            }
        });


    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        //mapView.onResume();
        super.onResume();
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.e(AppConstant.LOG.WPMAPDEBUG,"------------------");


            Log.e(AppConstant.LOG.WPMAPDEBUG, String.valueOf(bdLocation.getLocType()));
            StringBuffer sb = new StringBuffer(256);
            sb.append("error code:");
            sb.append(bdLocation.getLocType());
            sb.append("\nlatitude:");
            sb.append(bdLocation.getLatitude());
            sb.append("\nlongitude:");
            sb.append(bdLocation.getLongitude());
            sb.append("\nradius:");
            sb.append(bdLocation.getRadius());
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
//                sb.append()
            }
            Log.d(AppConstant.LOG.WPMAPDEBUG,"------------------");
            Log.d(AppConstant.LOG.WPMAPDEBUG, String.valueOf(bdLocation.getLatitude()));
            Log.d(AppConstant.LOG.WPMAPDEBUG, String.valueOf(bdLocation.getLongitude()));
            Log.d(AppConstant.LOG.WPMAPDEBUG,"------------------");

            tv_lat.setText(String.valueOf(bdLocation.getLatitude()));
            tv_lng.setText(String.valueOf(bdLocation.getLongitude()));

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            baiduMap.setMyLocationData(locData);
            MyLocationConfiguration.LocationMode locMode = MyLocationConfiguration.LocationMode.NORMAL;
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.loc_now);
            MyLocationConfiguration config = new MyLocationConfiguration(locMode ,true, mCurrentMarker);
            if(isFirstLoc){
                isFirstLoc = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);
                baiduMap.animateMapStatus(u);
            }
        }
    }

    private void initOption(){
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIgnoreKillProcess(true);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        locationClient.setLocOption(option);
    }





}
