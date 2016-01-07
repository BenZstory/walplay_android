package edu.ecustcs123.zhh.walplay.Utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by BenZ on 15.12.11.
 * zhengbin0320@gmail.com
 */
public class MarqueeTextView extends TextView{
    public MarqueeTextView(Context con){
        super(con);
    }
    public MarqueeTextView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public MarqueeTextView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }
    @Override
    public  boolean isFocused(){
        return true;
    }
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect){

    }
}
