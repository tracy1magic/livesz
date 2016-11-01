package com.bodai.livesz.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.bodai.livesz.MainActivity;
import com.bodai.livesz.R;
import com.bodai.livesz.base.BasePager;
import com.bodai.livesz.base.impl.GovAffairsPager;
import com.bodai.livesz.base.impl.HomePager;
import com.bodai.livesz.base.impl.NewsCenterPager;
import com.bodai.livesz.base.impl.SettingPager;
import com.bodai.livesz.base.impl.SmartServicePager;
import com.bodai.livesz.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * 主页面
 * Created by daibo on 2016/9/18.
 */
public class ContenFragment extends BaseFragment{

    private NoScrollViewPager mViewPager;
    private RadioGroup rgGroup;

    //五个标签页集合
    private ArrayList<BasePager> mPagers;
    @Override
    public View initView() {
        View view =  View.inflate(mActivity, R.layout.fragment_content,null);
        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vo_content);
        rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<BasePager>();
        //添加五个标签页
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());
        //底栏标签切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkdId) {
                switch (checkdId){
                    case R.id.rb_home:
                        mViewPager.setCurrentItem(0,false);//参数2,s设置切换无动画
                        break;
                    case R.id.rb_news:
                        mViewPager.setCurrentItem(1,false);
                        break;
                    case R.id.rb_smart:
                        mViewPager.setCurrentItem(2,false);
                        break;
                    case R.id.rb_gov:
                        mViewPager.setCurrentItem(3,false);
                        break;
                    case R.id.rb_setting:
                        mViewPager.setCurrentItem(4,false);
                        break;
                    default:
                        break;

                }

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPagers.get(position);
                pager.initData();
                if (position == 0 || position == mPagers.size()-1){
                    //首页和设置页禁用侧边栏
                    setSlidingMenuEnable(false);
                }else {
                    //其他页有侧边栏
                    setSlidingMenuEnable(true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //自动加载第一页数据
        mPagers.get(0).initData();
        //首页禁用侧边栏
        setSlidingMenuEnable(false);
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
    class ContentAdapter extends PagerAdapter{

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
            BasePager pager = mPagers.get(position);
            View view = pager.mRootView;//获取当前页面对象的布局
            pager.initData();//初始化数据，viewPager会默认加载下一个页面，为了节省流量和性能，不要在此处调用初始化数据的方法
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    //获取新闻中心页面
    public  NewsCenterPager getNewsCenterPager (){
        NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);
        return pager;
    }
}
