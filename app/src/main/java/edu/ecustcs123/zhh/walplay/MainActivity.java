package edu.ecustcs123.zhh.walplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ecustcs123.zhh.walplay.Utils.AppConstant;
import edu.ecustcs123.zhh.walplay.Utils.AppStatus;
import edu.ecustcs123.zhh.walplay.Utils.Mp3Info;
import edu.ecustcs123.zhh.walplay.Utils.MusicListUtil;
import edu.ecustcs123.zhh.walplay.Utils.WalAdapter;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private String[] drawerTitles;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private List<Mp3Info> mp3Infos;
    private ViewPager viewPager;
    private PlayerPanelFragment playerPanelFragment;
    private boolean bPlayerPanelFragment =false;
    private ListFragment.LBSReceiver lbsReceiver;

    private AppStatus appStatus;

    private FloatingActionButton fab;

    private RequestQueue requestQueue;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }

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
            mp3Infos= MusicListUtil.getMp3Infos(this);
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

        requestQueue = Volley.newRequestQueue(this);

        //界面下方始终有不变的playerPanelFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!bPlayerPanelFragment){
            playerPanelFragment = new PlayerPanelFragment();
            transaction.add(R.id.fragmentContainer_controlPanel,playerPanelFragment);
            transaction.commit();
            bPlayerPanelFragment = true;
        }

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(false);

        //viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //处理DrawerLayout相关
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();
                item.setChecked(true);
                switch (item.getItemId()){
                    case R.id.nav_test1:
                        testConn();
                    case R.id.nav_test2:
                        Toast.makeText(getApplicationContext(), "test2",Toast.LENGTH_SHORT).show();
                    case R.id.nav_sub_logout:
                        LogOut();
                }
                return false;
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);

        appStatus =(AppStatus) getApplicationContext();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.nav_tv_userName);

        if(appStatus.isLogin()){
            nav_user.setText(appStatus.getUserName());
        }else{
            nav_user.setText("请登录");
        }
    }

    private void init(){
//        View headerView = navigationView.findViewById()
        int a = navigationView.getHeaderCount();
        View headerView = navigationView.getHeaderView(0);
        final TextView nav_user = (TextView) headerView.findViewById(R.id.nav_tv_userName);

        nav_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppConstant.LOG.WPDLOGIN,"nav_user clicked");

                if(appStatus.isLogin()){//已经登陆，跳转到个人信息aty
                    Log.d(AppConstant.LOG.WPDStatus,"logged");

                }else{//尚未登陆，跳转到login.aty
                    Intent intent = new Intent();
                    intent.setClass(v.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //初始化登陆状态
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean logged = sharedPreferences.getBoolean(AppConstant.PrefKey.user_logged,false);
        if(logged) {
            appStatus.setIsLogin(true);
            String token = sharedPreferences.getString(AppConstant.PrefKey.user_token,"");
            String userName = sharedPreferences.getString(AppConstant.PrefKey.user_name,"");
            appStatus.setUserToken(token);
            appStatus.setUserName(userName);
        }
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadLoc();
            }
        });
    }

    private void uploadLoc(){
        //user have to be logged in
        if(!appStatus.isLogin()){
            Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        //check if the ll is suitable for uploading
        if(appStatus.getLocType()!=-1){
            Log.d(AppConstant.LOG.WPDStatus, "ready to upload lbs");
            Log.d(AppConstant.LOG.WPDStatus+"token", appStatus.getUserToken());
            final String token = appStatus.getUserToken();
            String url = AppConstant.URL_domain+"add_spot";

            HashMap<String, String> params = new HashMap<>();
            //params.put("loctype", String.valueOf(appStatus.getLocType()));
            params.put("latitude", String.valueOf(appStatus.getLocLatitude()));
            params.put("longitude", String.valueOf(appStatus.getLocLongitude()));
            params.put("radius", String.valueOf(appStatus.getLocRadius()));
            params.put("title","default_title");
            params.put("time","");

            Log.d(AppConstant.LOG.WPDJson+"url",url);
            Log.d(AppConstant.LOG.WPDJson+"json", String.valueOf(new JSONObject(params)));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url,
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(AppConstant.LOG.WPDJson+"response", String.valueOf(response));
                            int errCode = -1;
                            int spot_id = 0;
                            try {
                                errCode = response.getInt("code");
                                spot_id = response.getInt("spot_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (errCode == 0) {
                                Log.d(AppConstant.LOG.WPDVolley,"Spot_ID: "+String.valueOf(spot_id));
                                Toast.makeText(MainActivity.this, "已上传位置信息", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(AppConstant.LOG.WPDVolley,"Volley err: "+errCode);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(AppConstant.LOG.WPDJson+"error", "volley err...");
                            VolleyLog.e("Error: ", error.getMessage());
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", appStatus.getUserToken());
                    Log.d(AppConstant.LOG.WPDVolley+"header", String.valueOf(headers));
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
        }else{
            Log.d(AppConstant.LOG.WPDStatus, "upload denied");
            Toast.makeText(MainActivity.this, "地理信息不准确，无法上传", Toast.LENGTH_LONG).show();
        }

    }

    private void LogOut(){
        String url = AppConstant.URL_domain+"logout";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AppConstant.PrefKey.user_name);
        editor.remove(AppConstant.PrefKey.user_cell);
        editor.remove(AppConstant.PrefKey.user_token);
        editor.remove(AppConstant.PrefKey.user_logged);
        editor.apply();

        appStatus.setIsLogin(false);
        Toast.makeText(getApplicationContext(),"已注销",Toast.LENGTH_SHORT).show();
        View headerView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) headerView.findViewById(R.id.nav_tv_userName);
        nav_user.setText("请登录");

    }

    private void testConn(){
        String url = AppConstant.URL_domain;
        StringRequest mStringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "RESPONSE:" + response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"请求错误:" + error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(mStringRequest);
    }

    private void setupViewPager(ViewPager viewPager) {
        WalAdapter adapter = new WalAdapter(getSupportFragmentManager());
        adapter.addFragment(new ListFragment(), "列表");
        adapter.addFragment(new RecommenderFragment(),"跟踪");
        adapter.addFragment(new InfoFragment2(), "详情");
        adapter.addFragment(new MapFragment(), "地图");
        viewPager.setAdapter(adapter);
    }

}
