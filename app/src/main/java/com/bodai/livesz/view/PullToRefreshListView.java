package com.bodai.livesz.view;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bodai.livesz.R;

import java.util.Date;

/**
 * Created by daibo on 2016/10/8.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener{

    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;

    private int mCurrentState = STATE_PULL_TO_REFRESH;

    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY = -1;

    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;

    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private View pbProgress;

    private View mFooterView;

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }


    //初始化头布局
    private void initHeaderView(){
         mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header,null);
        this.addHeaderView(mHeaderView);
         tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
         tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
         ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
         pbProgress =  mHeaderView.findViewById(R.id.pb_loading);

        //隐藏下拉头布局
        mHeaderView.measure(0,0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
        initAnim();
        setCurrentTime();
    }

    //初始化脚布局
    private void initFooterView(){
        mFooterView = View.inflate(getContext(),R.layout.pull_to_refresh,null);
        this.addFooterView(mFooterView);
        mFooterView.measure(0,0);
        int mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);
        this.setOnScrollListener(this);

    }

    private void setCurrentTime(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            tvTime.setText(time);
        }
    }






    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1){
                    startY = (int) ev.getY();//当用户按住头条新闻的viewpager进行下拉时,ACTION_DOWN会被viewpager消费掉,
                    // 导致startY没有赋值,此处需要重新获取一下
                }
                if (mCurrentState == STATE_REFRESHING){
                    //如果正在刷新，跳出循坏
                    break;
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;
                int firstVisiblePosition = getFirstVisiblePosition();//当前显示的第一个item的位置
                //必须是下拉动作，且当前显示的是第一个item
                if (dy > 0 && firstVisiblePosition == 0){
                    int padding = dy - mHeaderViewHeight;//计算当前下拉控件的padding值
                    mHeaderView.setPadding(0,padding,0,0);
                    if (padding>0 && mCurrentState != STATE_RELEASE_TO_REFRESH){
                        //改为松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    }else if (padding<0 && mCurrentState != STATE_PULL_TO_REFRESH){
                        //改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                startY = -1;
                if (mCurrentState == STATE_RELEASE_TO_REFRESH){
                    mCurrentState = STATE_REFRESHING;
                    refreshState();
                    //完整展示头布局
                    mHeaderView.setPadding(0,0,0,0);
                    //4.进行回调
                    if (mListener != null){
                        mListener.onRefresh();
                    }


                }else if (mCurrentState == STATE_PULL_TO_REFRESH){
                    //隐藏头布局
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }


    private void initAnim(){
        animUp = new RotateAnimation(0,-180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        animDown = new RotateAnimation(-180,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    //根据当前状态刷新界面
    private void refreshState() {
        switch (mCurrentState){
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(VISIBLE);
                ivArrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新..");
                ivArrow.clearAnimation();
                pbProgress.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    //刷新结束，收起控件
    public void onRefreshComplete(boolean success){
        if (! isLoadMore){
            mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
            mCurrentState = STATE_PULL_TO_REFRESH;
            tvTitle.setText("下拉刷新");
            pbProgress.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);
            if (success){
                setCurrentTime();
            }else {
                //加载更多
                mFooterView.setPadding(0,-mHeaderViewHeight,0,0);
                isLoadMore = false;
            }
        }


    }

    //3.定义成员变量，接受监听对象
    private OnRefreshListener mListener;

    //2.暴露接口，设置监听
    public void setOnRefreshListener(OnRefreshListener listener){
        mListener = listener;

    }
    private boolean isLoadMore;//标记是否正在加载更多

    //滑动状态发生变化
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == SCROLL_STATE_IDLE){
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == getCount()-1 && ! isLoadMore);{
                //滑倒底了
                System.out.println("加载更多..");
                isLoadMore = true;
                mFooterView.setPadding(0,0,0,0);//显示加载更多的布局
                setSelection(getCount()-1);
                if (mListener != null){
                    mListener.onLoadMore();
                }
            }
        }

    }

    //滑动过程回调
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
    }

    //1.下拉刷新的回调接口
    public interface OnRefreshListener{
        public void onRefresh();
        public void onLoadMore();
    }
}
