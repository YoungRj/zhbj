package com.yrj.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yrj.zhbj.MainActivity;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.impl.NewsCenterPager;
import com.yrj.zhbj.domain.NewsMenu;

import java.util.ArrayList;

/**
 * 侧边栏Fragment，侧滑栏
 */
public class LeftMenuFragment extends BaseFragment {

    @ViewInject(R.id.lv_menu)
    private ListView lvList;

    private ArrayList<NewsMenu.NewsMenuData> data;

    private LeftMenuAdapter mAdapter;
    private int mCurrentPos;//当前点击位置

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);

        ViewUtils.inject(this, view);
        return view;
    }

    //设置侧边栏数据的方法
    //通过此方法可以从新闻中心页面把网络数据传递过来
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data){
        //当前选中位置归0，避免侧边栏选中位置和菜单详情也不同步
        mCurrentPos = 0;

        this.data = data;
        mAdapter = new LeftMenuAdapter();
        lvList.setAdapter(mAdapter);

        //点击侧边栏item
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mCurrentPos = position;//更新当前位置
                //刷新listView
                mAdapter.notifyDataSetChanged();
                //收回侧边栏，点击空白处时，如果当前侧边栏打开，则点击则关
                toggle();

                //修改菜单详情页
                setMenuDetailPager(position);
            }
        });
    }

    //修改菜单详情页
    private void setMenuDetailPager(int position) {
        //修改新闻中心帧布局
        //获取新闻中心对象。获取MainActivity--获取ContentFragment--获取NewsCenterPager。可通过zhbj的xmind查看关系
        MainActivity mainUI = (MainActivity) mActivity;
        ContentFragment cFragment = mainUI.getContentFragment();
        NewsCenterPager ncPager = cFragment.getNewsCenterPager();
        ncPager.setMenuDetailPager(position);
    }

    private void toggle() {
        MainActivity mainActivity = (MainActivity) mActivity;
        mainActivity.getSlidingMenu().toggle();//如果当前为开，则关；反之亦然
    }

    class LeftMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View containerView, ViewGroup viewGroup) {
            View view = View.inflate(mActivity, R.layout.list_item_left_menu, null);
            TextView tvView = view.findViewById(R.id.tv_menu);

            //设置TextView的可用不可用
            if(position == mCurrentPos) {
                //开启，选中
                tvView.setEnabled(true);
            } else{
                //关闭，选中
                tvView.setEnabled(false);
            }

            NewsMenu.NewsMenuData info = getItem(position);
            tvView.setText(info.title);//设置新闻中心侧边栏每个item名称

            return view;
        }
    }


}
