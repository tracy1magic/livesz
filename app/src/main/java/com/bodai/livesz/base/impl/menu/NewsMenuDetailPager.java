package com.bodai.livesz.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bodai.livesz.MainActivity;
import com.bodai.livesz.R;
import com.bodai.livesz.base.BaseMenuDetailPager;
import com.bodai.livesz.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * 菜单详情页-新闻
 *
 * ViewPagerIndicator使用流程
 * 1，引入库
 * 2.解决support-v4冲突（我的方式是把之前项目已经拥有的库的V4版本拷贝一份到这个库的lib文件夹下）
 * 3.参考例子程序
 * 5.在清单文件中增加样式
 * 6.背景可按喜好修改为白色
 * 7.修改样式，背景样式&文字样式
 * Created by daibo on 2016/9/21.
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener{
    @ViewInject(R.id.vp_news_menu_detail)

    private ViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;
    private ArrayList<NewsMenu.NewsTabData> mTabData;//页签网络数据
    private ArrayList<TabDetailpager> mPagers;//页签页面集合
    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        mTabData = children;
    }



    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        //初始化数据
        mPagers = new ArrayList<TabDetailpager>();
        for (int i=0;i<mTabData.size();i++){
            TabDetailpager pager = new TabDetailpager(mActivity,mTabData.get(i));
            mPagers.add(pager);
        }
        mViewPager.setAdapter(new NewsMenuDetailAdapter());
        mIndicator.setViewPager(mViewPager);//将ViewPager和指示器绑定在一起。需要注意：必须在viewPager设置完数据之后再绑定
        //设置页面滑动监听
//        mViewPager.addOnPageChangeListener(this);
        mIndicator.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("当前位置"+ position);
        if (position == 0){
            //开启侧边栏
            setSlidingMenuEnable(true);
        }else {
            //禁用侧边栏
            setSlidingMenuEnable(false);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //开启或禁用侧边栏
    protected void setSlidingMenuEnable(boolean enable){
        //获取侧边栏对象
        MainActivity MainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu  = MainUI.getSlidingMenu();
        if (enable){
            slidingMenu.setTouchModeAbove(slidingMenu.TOUCHMODE_FULLSCREEN);
        }else {
            slidingMenu.setTouchModeAbove(slidingMenu.TOUCHMODE_NONE);
        }

    }
    @OnClick(R.id.btn_next)
    public void nextPage(View view){
        //跳下一个
        int currentItem = mViewPager.getCurrentItem();
        currentItem ++;
        mViewPager.setCurrentItem(currentItem);

    }

    class NewsMenuDetailAdapter extends PagerAdapter{
        //制定指示器标题
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailpager pager = mPagers.get(position);
            View view = pager.mRootView;
            container.addView(view);
            pager.initData();
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
