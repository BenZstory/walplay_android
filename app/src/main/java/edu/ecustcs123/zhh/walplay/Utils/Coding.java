package edu.ecustcs123.zhh.walplay.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by BenZ on 15.12.30.
 * zhengbin0320@gmail.com
 */
public class Coding {



    public static String MD5(String s){
        try{
            //Create MD5 hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<messageDigest.length;i++){
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
