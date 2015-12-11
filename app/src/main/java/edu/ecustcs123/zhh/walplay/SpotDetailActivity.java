package edu.ecustcs123.zhh.walplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class SpotDetailActivity extends AppCompatActivity {
    private List<Mp3Info> mp3Infos;
    private SeekBar seekBar;
    private TextView tv_Title;
    private PlayerReceiver playerReceiver;
    private PlayingInfo playingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(AppConstant.KEY.PARCELABLE_PLAYINGINFO);
        Log.d(AppConstant.LOG.Com_Frag_Aty+"INTENT", String.valueOf(intent));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        PlayerPanelFragment playerPanelFragment = new PlayerPanelFragment();
        playerPanelFragment.setArguments(bundle);           //获得playingInfo
        Log.d(AppConstant.LOG.Com_Frag_Aty+"_SAVING", String.valueOf(bundle));
        Log.d(AppConstant.LOG.Com_Frag_Aty, "putBundle_spotAty");
        transaction.add(R.id.fragmentContainer_controlPanel, playerPanelFragment);
        transaction.commit();


        TabHost T;
    }
}