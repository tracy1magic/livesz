package com.bodai.livesz.base.impl.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bodai.livesz.NewsDetailActivity;
import com.bodai.livesz.R;
import com.bodai.livesz.base.BaseMenuDetailPager;
import com.bodai.livesz.domain.NewsMenu;
import com.bodai.livesz.domain.NewsTabBean;
import com.bodai.livesz.global.GlobalConstants;
import com.bodai.livesz.utils.CacheUtils;
import com.bodai.livesz.utils.PrefUtils;
import com.bodai.livesz.view.PullToRefreshListView;
import com.bodai.livesz.view.TopNewsViewPager;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/22.
 */
public class TabDetailpager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData mTabData;//单个页签网络数据

    private NewsAdapter mNewsAdapter;

    private ArrayList<NewsTabBean.NewsData> mNewsList;

    @ViewInject(R.id.lv_list)
    private PullToRefreshListView lvList;

    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;

    private String mUrl;
    private ArrayList<NewsTabBean.TopNews> mTopNews;

    private String mMoreUrl;

    private Handler mHandler;

    public TabDetailpager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_tab_detail,null);
        ViewUtils.inject(this,view);
        //给listView添加头布局
        View mHeaderView = View.inflate(mActivity,R.layout.list_item_header,null);
        ViewUtils.inject(this,mHeaderView);//此处不要忘记将头布局也注入
        lvList.addHeaderView(mHeaderView);
        //前端界面设置回调
        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                //判断是否有下一页数据
                if (mMoreUrl != null){
                    getMoreFromServer();
                }else {
                    Toast.makeText(mActivity,"没有更多数据了",Toast.LENGTH_SHORT).show();
                    lvList.onRefreshComplete(true);
                }
            }
        });

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int headerViewsCount = lvList.getHeaderViewsCount();//获取头布局数量
                i = i-headerViewsCount;
                System.out.println("第"+ i+"被点击了");
                NewsTabBean.NewsData news = mNewsList.get(i);
                String readIds = PrefUtils.getString(mActivity,"read_ids","");
                if (!readIds.contains(news.id + "")){
                    readIds = readIds + news.id + ",";
                    PrefUtils.setString(mActivity,"read_ids",readIds);
                }
                //做被点击效果
                TextView tvTitle  = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
                //跳新闻详情
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url",news.url);
                mActivity.startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(mUrl,mActivity);
        if (!TextUtils.isEmpty(cache)){
            processData(cache,false);
        }
        getDataFromServer();
    }

    private void getDataFromServer(){
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,false);
                CacheUtils.setCache(mUrl,result,mActivity);

                //收起下拉刷新控件
                lvList.onRefreshComplete(true);

            }
            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                Toast.makeText(mActivity,s,Toast.LENGTH_SHORT).show();
                lvList.onRefreshComplete(false);

            }
        });
    }

    protected void getMoreFromServer(){
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,true);
//                CacheUtils.setCache(mUrl,result,mActivity);

                //收起下拉刷新控件
//                lvList.onRefreshComplete();

            }
            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                Toast.makeText(mActivity,s,Toast.LENGTH_SHORT).show();
//                lvList.onRefreshComplete();

            }
        });

    }
    protected void processData(String result,boolean isMore ){
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);
        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)){
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        }else {
            mMoreUrl = null;
        }
        if (!isMore){
            //头条新闻填充数据
            mTopNews = newsTabBean.data.topnews;
            if (mTopNews != null){
                mViewPager.setAdapter(new TopNewsAdapter());
                mIndicator.setViewPager(mViewPager);
                //快照方式展示
                mIndicator.setSnap(true);
                mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        //跟新头条新闻标题
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        tvTitle.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //跟新第一个头条新闻的标题
                tvTitle.setText(mTopNews.get(0).title);
                //默认让第一个选中（解决页面销毁后重新初始化时，indicator停留原位置）
                mIndicator.onPageSelected(0);
            }
            //列表新闻
            mNewsList = newsTabBean.data.news;
            if (mNewsList != null){
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }
            if (mHandler == null){
                mHandler = new Handler(){
                    public void handleMessage(android.os.Message msg){
                        int curentItem = mViewPager.getCurrentItem();
                        curentItem ++;
                        if (curentItem>mTopNews.size()-1){
                            curentItem = 0;//最后跳到第一页
                        }
                        mViewPager.setCurrentItem(curentItem);
                        mHandler.sendEmptyMessageDelayed(0,3000);
                    }
                };
                mHandler.sendEmptyMessageDelayed(0,3000);
                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            default:
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                break;
                            case MotionEvent.ACTION_UP:
                                mHandler.sendEmptyMessageDelayed(0,3000);
                                break;
                        }
                        return false;
                    }
                });
            }
        }else {
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews);
            mNewsAdapter.notifyDataSetChanged();
        }


        //头条新闻填充数据
       mTopNews = newsTabBean.data.topnews;
        if (mTopNews != null){
            mViewPager.setAdapter(new TopNewsAdapter());
            mIndicator.setViewPager(mViewPager);
            //快照方式展示
            mIndicator.setSnap(true);
            mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //跟新头条新闻标题
                    NewsTabBean.TopNews topNews = mTopNews.get(position);
                    tvTitle.setText(topNews.title);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //跟新第一个头条新闻的标题
            tvTitle.setText(mTopNews.get(0).title);
            //默认让第一个选中（解决页面销毁后重新初始化时，indicator停留原位置）
            mIndicator.onPageSelected(0);
        }
        //列表新闻
        mNewsList = newsTabBean.data.news;
        if (mNewsList != null){
            mNewsAdapter = new NewsAdapter();
            lvList.setAdapter(mNewsAdapter);
        }

    }

    //头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter{
        private BitmapUtils mBitmapUtils;


        public TopNewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            //加载中的默认图片
            mBitmapUtils.configDefaultLoadingImage(R.drawable.def);
        }


        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
//            view.setImageResource(R.drawable.topnews_item_default);
            //设置图片缩放方式，宽高填充父控件
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            //图片下载链接
            String imageUrl = mTopNews.get(position).topimage;

            //将下载的图片设置给imageview，避免内存溢出，缓存数据（BitmapUtils框架）
            mBitmapUtils.display(view,imageUrl);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    class NewsAdapter extends BaseAdapter{
        private BitmapUtils mBitmapUtils;
        public NewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.def);
        }
        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int i) {
            return mNewsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = View.inflate(mActivity,R.layout.list_item_news,null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
                holder.tvDate = (TextView) view.findViewById(R.id.tv_date);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            NewsTabBean.NewsData news = getItem(i);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);
            //根据本地记录标记已读未读
            String readIds = PrefUtils.getString(mActivity,"read_ids","");
            if (readIds.contains(news.id+"")){
                holder.tvTitle.setTextColor(Color.GRAY);
            }else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }
            mBitmapUtils.display(holder.ivIcon,news.listimage);
            return view;
        }
    }
    static class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

}
