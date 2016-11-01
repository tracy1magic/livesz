package com.bodai.livesz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bodai.livesz.utils.PrefUtils;

import java.util.ArrayList;


/**
 * Created by daibo on 2016/9/17.
 */
public class GuideActivity extends Activity{
    private ViewPager mViewPager;
    //小圆点
    private ImageView ivRedPoint;

    //小红点移动距离
    private int mPointDis;

    private Button btnStart;

    private LinearLayout llContainer;

    //imageView集合
    private ArrayList<ImageView> mImageViewList;

    //引导页图片id数组
    private int[] mImageIds  = new int[]{
            R.drawable.guide_1,
            R.drawable.guide_2,
            R.drawable.guide_3
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏，这个方法必须在setContentView之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
        btnStart = (Button) findViewById(R.id.btn_start);
        initdata();
        //设置数据
        mViewPager.setAdapter(new GuideAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当页面滑动过程中的回调
                //跟新小红点位置
                int leftMargin = (int) (mPointDis * positionOffset)+ position * mPointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                //修改左边距
                params.leftMargin = leftMargin;
                //重新设置布局参数
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中
                //最后一个页面显示开始体验按钮
                if (position == mImageViewList.size()-1){
                    btnStart.setVisibility(View.VISIBLE);
                }else {
                    btnStart.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //页面状态发生变化的回调
            }
        });
        //计算两个圆点的距离

        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPointDis = llContainer.getChildAt(1).getLeft()-llContainer.getChildAt(0).getLeft();
                System.out.print("圆点距离："+ mPointDis);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跟新sp,即不是第一次进入了
                PrefUtils.setBoolean(getApplicationContext(),"is_first_enter",false);
                //跳到主页面
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }



    //初始化数据
    private void initdata(){
        mImageViewList = new ArrayList<ImageView>();
        for (int i = 0;i<mImageIds.length;i++){
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);

            //设置图片（shape行形状）
            point.setImageResource(R.drawable.shape_point_gray);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i>0){
                //设置左边距
                params.leftMargin = 10;
            }

            //设置布局参数
            point.setLayoutParams(params);



            //给容器添加原点
            llContainer.addView(point);


        }

    }
    class GuideAdapter extends PagerAdapter{

        //item的个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
