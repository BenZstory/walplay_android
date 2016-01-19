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
