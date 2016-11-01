package com.bodai.livesz.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bodai.livesz.MainActivity;
import com.bodai.livesz.R;
import com.bodai.livesz.base.impl.NewsCenterPager;
import com.bodai.livesz.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 侧边栏
 * Created by daibo on 2016/9/18.
 */
public class LeftMenuFragment extends BaseFragment {
    private LeftMenuAdapter mAdapter;
    private int mCurrentPos;
    private ArrayList<NewsMenu.NewsMenuData> mNewsMenuDatas;
    @ViewInject(R.id.lv_list)
    private ListView lvList;
    @Override
    public View initView() {
        View view =  View.inflate(mActivity, R.layout.fragment_left_menu,null);
       // lvList = (ListView) view.findViewById(R.id.lv_list);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {

    }
    //给侧边栏设置数据
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData>data){
        mCurrentPos = 0;//当前选中位子归零
        //跟新页面
        mNewsMenuDatas = data;
        mAdapter = new LeftMenuAdapter();
        lvList.setAdapter(mAdapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCurrentPos = position;//跟新当前被选中位置
                mAdapter.notifyDataSetChanged();//刷新listView
                //收起侧边栏
                toggle();
                //侧边栏点击后，遥修改新闻中心的FramLayout中的内容
                setCurrentDetailPager(position);
            }
        });

    }

    //设置当前菜单详情页
    protected void setCurrentDetailPager(int position){
        //获取新闻中心
        MainActivity mainUI = (MainActivity) mActivity;
        //获取ContentFragment
        ContenFragment fragment = mainUI.getContenFragment();
        //获取NewsCententPager
        NewsCenterPager newsCenterPager = fragment.getNewsCenterPager();
        //修改新闻中心的FragmentLayout的布局
        newsCenterPager.setCurrentDetailPager(position);

    }
    //打开或者关闭侧边栏
    protected void toggle(){
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();

    }
    class LeftMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mNewsMenuDatas.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return mNewsMenuDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(mActivity,R.layout.list_item_left_menu,null);
            TextView tvMenu = (TextView) view.findViewById(R.id.tv_menu);
            NewsMenu.NewsMenuData item = getItem(position);
            tvMenu.setText(item.title);
            if (position == mCurrentPos){
                //被选中
                tvMenu.setEnabled(true);
            }else {
                //未选中
                tvMenu.setEnabled(false);
            }
            return view;
        }
    }
}
