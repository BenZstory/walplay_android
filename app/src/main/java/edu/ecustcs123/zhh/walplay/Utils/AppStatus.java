package edu.ecustcs123.zhh.walplay.Utils;

import android.app.Application;

/**
 * Created by BenZ on 16.1.5.
 * zhengbin0320@gmail.com
 */
public class AppStatus extends Application {
    private boolean isLogin = false;
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
}
