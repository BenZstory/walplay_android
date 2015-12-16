package edu.ecustcs123.zhh.walplay;

import android.app.Service;
import android.content.Intent;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationConfiguration;

public class LBSService extends Service {
    private LocationClient locationClient = null;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private double latitude;
    private double Longitude;
    private int locType;
    private static int interval;

    public LBSService() {
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            Log.d(AppConstant.LOG.WPLBSDEBUG,"in handler");
            if(msg.what == 1){
//                Log.d(AppConstant.LOG.WPLBSDEBUG,"in handler and msg == 1");
                initOption();
                locationClient.start();
                handler.sendEmptyMessageDelayed(1, interval);
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstant.LOG.WPLBSDEBUG,"LBS starting 2.....");
        SDKInitializer.initialize(getApplicationContext());
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myLocationListener);
        interval = 3000;        //TODO 定位时间间隔默认3s，以后根据intent调
//        initOption();
//        locationClient.start();
        handler.sendEmptyMessage(1);
        return super.onStartCommand(intent, flags, startId);
    }

    private class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(AppConstant.LOG.WPLBSDEBUG,"Receive location");
            Intent intent = new Intent(AppConstant.ACTION.GET_LOC);
            intent.putExtra("locTyep",bdLocation.getLocType());
            intent.putExtra("latitude",bdLocation.getLatitude());
            intent.putExtra("longitude",bdLocation.getLongitude());
            intent.putExtra("radius",bdLocation.getRadius());
            sendBroadcast(intent);
        }
    }

    private void initOption(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode
                .Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");       //可选，默认gcj02，设置返回的定位结果坐标系
        //int span=1000;
        option.setScanSpan(0);              //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);            //开GPS
        option.setLocationNotify(true);     //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);  //设置是否在stop的时候杀死定位service，默认杀死
        option.setEnableSimulateGps(false);
        option.setIsNeedLocationDescribe(false);
        option.setIsNeedLocationPoiList(false);
        locationClient.setLocOption(option);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
