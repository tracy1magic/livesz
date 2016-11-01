package com.bodai.livesz.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by daibo on 2016/10/6.
 */
public class TopNewsViewPager extends ViewPager{
    private int startX;
    private int startY;
    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int) event.getX();
                int endY = (int) event.getY();
                int dx = endX - startX;
                int dy = endY - startY;
                if (Math.abs(dy) < Math.abs(dx)){
                    int currentItem = getCurrentItem();
                    //认为用户是左右滑动
                    if (dx>0){
                        //向右滑
                        if (currentItem==0){
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }else {
                            //向左滑
                            int count = getAdapter().getCount();//item总数
                            if (currentItem ==  count -1){
                                //最后一个页面，需要拦截
                                getParent().requestDisallowInterceptTouchEvent(false);
                            }
                        }
                    }
                }else {
                    //上下滑动,需要拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchGenericFocusedEvent(event);
    }
}
