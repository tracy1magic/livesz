package com.bodai.livesz.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by daibo on 2016/9/20.
 */
public class NoScrollViewPager extends ViewPager{
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //时间拦截


    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return false;//不拦截子控件的事件
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //重写此方法，触摸时不反馈
        return true;
    }
}
