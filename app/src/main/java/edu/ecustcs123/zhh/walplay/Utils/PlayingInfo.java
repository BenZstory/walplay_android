package edu.ecustcs123.zhh.walplay.Utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by BenZ on 15.12.10.
 * zhengbin0320@gmail.com
 */
public class PlayingInfo implements Parcelable {
    private String musicTitle;
    private int listPos;
    private String url;
    private boolean isPlaying;
    private boolean isPause;
    private long currentTime;        //记录当前播放进度
    private long duration;           //音乐长度int
    private int playMode;

    @Override
    public int describeContents() {
        return 0;//不用管
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        dest.writeString(getMusicTitle());
        dest.writeInt(getListPos());
        dest.writeString(getUrl());
        dest.writeBooleanArray(new boolean[]{isPlaying(), isPause()});
        dest.writeLong(getCurrentTime());
        dest.writeLong(getDuration());
        dest.writeInt(getPlayMode());
    }

    public static final Creator<PlayingInfo> CREATOR = new Creator<PlayingInfo>() {
        @Override
        public PlayingInfo createFromParcel(Parcel source) {
            return new PlayingInfo(source);
        }

        @Override
        public PlayingInfo[] newArray(int size) {
            return new PlayingInfo[size];
        }
    };

    private PlayingInfo(Parcel in){
        musicTitle = in.readString();
        listPos = in.readInt();
        url = in.readString();
        boolean[] tmp = new boolean[2];
        in.readBooleanArray(tmp);
        isPlaying = tmp[0];
        isPause = tmp[1];
        currentTime = in.readLong();
        duration = in.readLong();
        playMode = in.readInt();
    }

    public PlayingInfo() {
        //默认构造函数
        setMusicTitle("Default Title Text");
        setListPos(-1);
        setIsPlaying(false);
        setIsPause(false);
        setCurrentTime(0);
        setDuration(100);
        setPlayMode(0);
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
