package edu.ecustcs123.zhh.walplay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;

public class LBSService extends Service {
    private LocationClient locationClient = null;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private MyTestListener myTestListener = new MyTestListener();
    private double latitude;
    private double Longitude;
    private int locType;
    private boolean isNotifying = false;
    private int lastLocArea = -1;
    private static int interval;
    public boolean isInSpot = false;

    public LBSService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstant.LOG.WPLBSDEBUG,"LBS starting 2.....");
        SDKInitializer.initialize(getApplicationContext());
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myLocationListener);
        interval = 3000;        //TODO 定位时间间隔默认3s，以后根据intent传值来调节
        initOption(interval);
        locationClient.start();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.ACTION.CHANGE_SPOT_STATUS);
        registerReceiver(myTestListener,intentFilter);
        //handler.sendEmptyMessage(1);
        return super.onStartCommand(intent, flags, startId);
    }

    private class MyTestListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isInSpot) isInSpot = false;
            else isInSpot = true;
        }
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int locArea = -1;
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            LatLng ll = new LatLng(latitude,longitude);
            Log.d(AppConstant.LOG.WPLBSDEBUG,"Receive location");
            Intent intent = new Intent(AppConstant.ACTION.GET_LOC);
            intent.putExtra("locType",bdLocation.getLocType());
            Log.d(AppConstant.LOG.WPLBSDEBUG+"err", String.valueOf(bdLocation.getLocType()));
            intent.putExtra("latitude",bdLocation.getLatitude());
            intent.putExtra("longitude",bdLocation.getLongitude());
            intent.putExtra("radius",bdLocation.getRadius());
            sendBroadcast(intent);

            //TODO 判断是否进入新spot区域并推送消息,包括locArea以及简介
            //没有在spot外，且与上次locArea不同则推送，没进入一个locArea只推送一次
            locArea = getLocArea(ll);
            Log.d(AppConstant.LOG.WPLBSDEBUG+"_locArea", String.valueOf(locArea));
            Log.d(AppConstant.LOG.WPLBSDEBUG+"_lastArea", String.valueOf(lastLocArea));

            if(locArea != lastLocArea){
                //locArea == -1时，从景区走出，否则即进入了新景区，两种情况都要更新UI
                lastLocArea = locArea;
                Intent crier = new Intent(AppConstant.ACTION.NOTIFY_SPOT);
                crier.putExtra("locArea",locArea);
                Log.d(AppConstant.LOG.WPLBSDEBUG, "Spot Changed.");
                sendBroadcast(crier);
            }
        }
    }

    /**
     * 返回所在景点区域代号
     * @return
     * -1代表不在景点spot中
     */
    public int getLocArea(LatLng posNow){
        //以下代码仅为测试，正常应从服务器后端判断并返回spotId
        //spot为华理一教位置，大概
        double lat = 31.149561;
        double lng = 121.432409;
        LatLng spot = new LatLng(lat,lng);
        double distance = DistanceUtil.getDistance(posNow,spot);
        Log.d(AppConstant.LOG.WPLBSDEBUG+"_dis", String.valueOf(distance));
        /*if(isInSpot) return 1;
        else return -1;*/
        if(distance<50) return 1;
        else return -1;
    }

    /**
     * 百度地图定位SDK设置的初始化
     * @param span
     */
    private void initOption(int span){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode
                .Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");       //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(span);           //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
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
