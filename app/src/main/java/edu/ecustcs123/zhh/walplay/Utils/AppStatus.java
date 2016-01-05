package edu.ecustcs123.zhh.walplay.Utils;

/**
 * Created by Benjamin Zheng on 16.1.5.
 */
public class AppStatus {
    private boolean isLogin;
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
