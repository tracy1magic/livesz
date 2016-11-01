package com.bodai.livesz;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.bodai.livesz.fragment.ContenFragment;
import com.bodai.livesz.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * Created by Administrator on 2016/9/17.
 */
public class MainActivity extends SlidingFragmentActivity{
    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
    private static final String TAG_CONTENT = "TAG_CONTENT";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        setContentView(R.layout.activyty_main);

        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.getTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏触摸
        slidingMenu.setBehindOffset(500);//屏幕预留
        initFragment();
    }

    //初始化fragment
    private void initFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_left_menu,new LeftMenuFragment(),"TAG_LEFT_MENU");
        transaction.replace(R.id.fl_main,new ContenFragment(),"TAG_CONTENT");
        //提交事务
        transaction.commit();
        //Fragment fragment = fm.findFragmentByTag(TAG_LEFT_MENU);
    }
    //获取侧边栏Fragment对象
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
        return fragment;
    }
    //获取主页Fragment对象
    public ContenFragment getContenFragment(){
        FragmentManager fm = getSupportFragmentManager();
        ContenFragment fragment = (ContenFragment) fm.findFragmentByTag(TAG_CONTENT);
        return fragment;
    }
}
