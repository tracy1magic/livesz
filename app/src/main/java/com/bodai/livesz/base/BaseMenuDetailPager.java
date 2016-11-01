package com.bodai.livesz.base;

import android.app.Activity;
import android.view.View;

/**
 * 菜单详情页基类
 * Created by daibo on 2016/9/21.
 */
public abstract class BaseMenuDetailPager {
    public View mRootView;
    public Activity mActivity;
    public BaseMenuDetailPager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }
    //初始化布局，必须子类实现
    public abstract View initView();
    //初始化数据
    public void initData(){

    }
}
