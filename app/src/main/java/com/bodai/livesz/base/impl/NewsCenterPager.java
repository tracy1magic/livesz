package com.bodai.livesz.base.impl;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bodai.livesz.MainActivity;
import com.bodai.livesz.base.BaseMenuDetailPager;
import com.bodai.livesz.base.BasePager;
import com.bodai.livesz.base.impl.menu.InteractMenuDetailPager;
import com.bodai.livesz.base.impl.menu.NewsMenuDetailPager;
import com.bodai.livesz.base.impl.menu.TopicMenuDetailPager;
import com.bodai.livesz.domain.NewsMenu;
import com.bodai.livesz.fragment.LeftMenuFragment;
import com.bodai.livesz.global.GlobalConstants;
import com.bodai.livesz.utils.CacheUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/19.
 */
public class NewsCenterPager extends BasePager {

    private ArrayList<BaseMenuDetailPager> mBaseMenuDetailPagers;//菜单详情页集合

    private NewsMenu mNewsData;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //要给帧布局填充数据对象
//        TextView view = new TextView(mActivity);
//        view.setText("新闻中心");
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);
//
//        flContent.addView(view);
        //修改页面标题
        tvTitle.setText("新闻");
        //显示菜单按钮
        btMenu.setVisibility(View.VISIBLE);

        //先判断有没有缓存
        String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL,mActivity);
        if (!TextUtils.isEmpty(cache)){
            System.out.println("存在缓存..");
            processData(cache);
        }
            //请求服务器，获取数据 xUtils
            getDataFromServer();


    }
    private void getDataFromServer(){
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                //请求成功
                String result = (String) responseInfo.result;//获取服务器返回结果
                System.out.println("服务器返回结果：" + result);
                //JsonObject,Gson
                processData(result);
                //写缓存
                CacheUtils.setCache(GlobalConstants.CATEGORY_URL,result,mActivity);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                Toast.makeText(mActivity,s,Toast.LENGTH_SHORT).show();

            }
        });
    }
    //解析数据
    protected void processData(String json){
        Gson gson = new Gson();
        mNewsData  = gson.fromJson(json, NewsMenu.class);
        System.out.print("解析结果" + mNewsData);
        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置数据
        fragment.setMenuData(mNewsData.data);
        //初始化四个菜单详情页
        mBaseMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
        mBaseMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));
        mBaseMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));

        mBaseMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));
        //设置新闻菜单详情页为默认页面
        setCurrentDetailPager(0);
    }

    //设置菜单详情页
    public void setCurrentDetailPager(int position){
        //重新给FrameLayout添加内容
        BaseMenuDetailPager pager = mBaseMenuDetailPagers.get(position);
        View view = pager.mRootView;
        //清楚之前的布局
        flContent.removeAllViews();
        //给帧布局添加布局
        flContent.addView(view);
        //初始化页面数据
        pager.initData();
        //跟新标题
        tvTitle.setText(mNewsData.data.get(position).title);

    }
}
