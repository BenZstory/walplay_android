package edu.ecustcs123.zhh.walplay;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    //List Panel
    private ListView mMusicListView;//列表view
    private List<Mp3Info> mp3Infos;//保存mp3info的列表
    private SimpleAdapter mAdapter;//列表adapter
    private PlayerPanelFragment playerPanelFragment;

    @Override
    public void onResume() {
        super.onResume();
    }

    //Control Panel
    private Button previousBtn;//上一首
    private Button nextBtn;//下一首
    private Button playBtn;//播放暂停
    private Button btnPlayMode;
    private ImageButton modeIBtn;//播放方式
    private TextView titleTxtView;
    private SeekBar seekBar;
    //-----ON CREATE-----
    protected void onCreate(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_list,container,false);
        //init();
        //initWidgetPointer();//FindViewById
        //setOnClickListener();//添加监听器
        mMusicListView = (ListView) view.findViewById(R.id.lv_musiclistview);
        mMusicListView.setOnItemClickListener(new MusicItemOnClickListener());//注册click监听器，自定义实现listener
        mp3Infos = MusicListUtil.getMp3Infos(getActivity());//从数据库获取歌曲列表
        setListAdapter(MusicListUtil.getMusicMaps(mp3Infos));//bind mp3 list
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        PlayerPanelFragment playerPanelFragment = new PlayerPanelFragment();
        transaction.add(R.id.fragmentContainer_controlPanel, playerPanelFragment);
        transaction.commit();
    }

    /**
     * Get clicked pos and Start the PlayerService to play the musicpiece
     */
    private class MusicItemOnClickListener implements AdapterView.OnItemClickListener{
        //TODO:不仅只是播放，还应有别的按钮，如何使item部分被点击
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("WP_Clicked------", String.valueOf(position));
            Intent intent  = new Intent();
            intent.putExtra("MSG", AppConstant.PlayerMsg.PLAY_MSG);
            intent.putExtra("listPos", position);
            intent.setAction(AppConstant.ACTION.MUSIC_SERVICE);
            intent.setPackage("edu.ecustcs123.zhh.walplay");
            getActivity().startService(intent);
            final Intent intent2 = new Intent();
            intent2.setClass(getActivity(), SpotDetailActivity.class);
            intent2.putExtra("listPos", position);
            startActivity(intent2);
//            Log.d("startPlaying-->", String.valueOf(position));
        }
    }

    public void setListAdapter(List<HashMap<String,Object>> list) {
        //TODO:ibtn会抢占焦点，先把btn去掉，以后再做决断
        mAdapter = new SimpleAdapter(getActivity(),list,R.layout.music_item,
                new String[] {"title","artist","duration"},
                new int[] {R.id.tv_itemMusicName,R.id.tv_itemMusicInfo,R.id.tv_itemMusicDuration}
        );
        mMusicListView.setAdapter(mAdapter);
    }
}

