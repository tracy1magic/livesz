package com.bodai.livesz.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bodai.livesz.MainActivity;
import com.bodai.livesz.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by daibo on 2016/9/19.
 */
public class BasePager{

    public Activity mActivity;
    public TextView tvTitle;
    public ImageView btMenu;
    public FrameLayout flContent;

    public View mRootView;

    /**
     * 五个标签页的基类
     * @param activity
     */

    public BasePager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }
    //初始化布局
    public View initView(){
        View view = View.inflate(mActivity, R.layout.base_pager,null);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        btMenu = (ImageView) view.findViewById(R.id.btn_menu);
        flContent = (FrameLayout) view.findViewById(R.id.fl_content);
        btMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        return view;
    }
    protected void toggle(){
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();

    }

    //初始化数据
    public void initData(){

    }
}
