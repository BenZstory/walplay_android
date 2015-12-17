package edu.ecustcs123.zhh.walplay;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState){
        Log.d(AppConstant.LOG.Fragment_onview, "createView");
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mMusicListView = (ListView) view.findViewById(R.id.lv_musiclistview);
        mMusicListView.setOnItemClickListener(new MusicItemOnClickListener());//注册click监听器，自定义实现listener
        mp3Infos = MusicListUtil.getMp3Infos(getActivity());//从数据库获取歌曲列表
        setListAdapter(MusicListUtil.getMusicMaps(mp3Infos));//bind mp3 list
    }

    //-----ON CREATE-----
    protected void onCreate(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            //更新PlayPanelFragment 跳转到详情界面
            getFragmentManager().beginTransaction().replace(R.id.fragmentContainer_controlPanel,new PlayerPanelFragment()).addToBackStack(null).commit();
            getFragmentManager().beginTransaction().replace(R.id.Info_Fragment,new InfoFragment()).addToBackStack(null).commit();
            ((ViewPager)getActivity().findViewById(R.id.MainViewPager)).setCurrentItem(1);



            /*getFragmentManager().beginTransaction().replace(R.id.List_Fragment,new InfoFragment()).addToBackStack(null).commit();*/
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

