package com.bodai.livesz.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.bodai.livesz.base.BasePager;

/**
 * Created by Administrator on 2016/9/19.
 */
public class HomePager extends BasePager {

    public HomePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //要给帧布局填充数据对象
        TextView view = new TextView(mActivity);
        view.setText("首页");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        flContent.addView(view);
        //修改页面标题
        tvTitle.setText("深圳");

        //隐藏菜单按钮
        btMenu.setVisibility(View.GONE);
    }
}
