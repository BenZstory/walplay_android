package edu.ecustcs123.zhh.walplay.Utils;

import android.app.Application;

/**
 * Created by BenZ on 16.1.5.
 * zhengbin0320@gmail.com
 */
public class AppStatus extends Application {
    private boolean isLogin = false;
    private String userToken;
    private String userName;
    private int locType = -1;
    private double locLatitude;
    private double locLongitude;
    private double locRadius;

    public int getLocType() {
        return locType;
    }

    public double getLocLatitude() {
        return locLatitude;
    }

    public double getLocLongitude() {
        return locLongitude;
    }

    public double getLocRadius() {
        return locRadius;
    }

    public void setLocType(int locType) {

        this.locType = locType;
    }

    public void setLocLatitude(double locLatitude) {
        this.locLatitude = locLatitude;
    }

    public void setLocLongitude(double locLongitude) {
        this.locLongitude = locLongitude;
    }

    public void setLocRadius(double locRadius) {
        this.locRadius = locRadius;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
