package com.yrj.zhbj.base.impl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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
import com.yrj.zhbj.NewsDetailActivity;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.domain.NewsMenu;
import com.yrj.zhbj.domain.NewsTab;
import com.yrj.zhbj.global.GlobalConstants;
import com.yrj.zhbj.utils.CacheUtils;
import com.yrj.zhbj.utils.PrefUtils;
import com.yrj.zhbj.view.RefreshListView;
import com.yrj.zhbj.view.TopNewsViewPager;

import java.util.ArrayList;

/**
 * 页签详情页，“北京”“中国”“国际”
 * 继承BaseMenuDetailPager，从代码来讲比较简洁，但不符合面向对象
 * 但当前页不属于菜单详情页，这是个干爹
 */
public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData newsTabData;
//    private TextView view;

    @ViewInject(R.id.vp_tab_detail)
    private TopNewsViewPager mViewPager;
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;
    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;
    @ViewInject(R.id.lv_list)
    private RefreshListView lvList;

    private String mUrl;
    private ArrayList<NewsTab.TopNews> topNewsList;
    private ArrayList<NewsTab.News> newsList;
    private String moreUrl;//加载更多的URL

    private NewsAdapter newsAdapter;
    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        this.newsTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + newsTabData.url;
    }

    @Override
    public View initViews() {
//        view = new TextView(mActivity);
//        view.setText("页签");
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        View headerView = View.inflate(mActivity, R.layout.list_item_header, null);
        ViewUtils.inject(this, view);
        ViewUtils.inject(this, headerView);

        lvList.addHeaderView(headerView);

        //自定义接口
        lvList.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            //加载更多
            @Override
            public void onLoadMore() {
                if (!TextUtils.isEmpty(moreUrl)) {
                    getMoreDataFromServer();
                } else {
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    lvList.onRefreshComplete();//隐藏脚布局控件
                }

            }
        });

        //点击item保存id，标记已读未读
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int headerViewsCount = lvList.getHeaderViewsCount();//获取头布局个数
                position -= headerViewsCount;
                NewsTab.News news = newsList.get(position);
                String readIds = PrefUtils.getString(mActivity, "read_ids", "");
                if (!readIds.contains(news.id)) {
                    readIds += news.id + ",";
                    PrefUtils.setString(mActivity, "read_ids", readIds);
                }
                //全局刷新，刷新ListView
//                newsAdapter.notifyDataSetChanged();

                //点击局部刷新，标记为灰色，表示已读
                ((TextView) view.findViewById(R.id.iv_title)).setTextColor(Color.GRAY);

                //点击每条新闻标题，跳到新闻的详情页
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
//        view.setText(newsTabData.title);
        String cache = CacheUtils.getCache(mActivity, mUrl, null);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache, false);
        }
        getDataFromServer();
    }

    //获取页签详情页服务器数据
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, false);
                CacheUtils.setCache(mActivity, mUrl, result);
                lvList.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                lvList.onRefreshComplete();
            }
        });
    }

    //获取加载更多服务器数据
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, moreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, true);
                lvList.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
                lvList.onRefreshComplete();
            }
        });
    }

    //解析数据，isMore：是否加载更多数据
    private void processData(String result, boolean isMore) {
        Gson gson = new Gson();
        NewsTab newsTab = gson.fromJson(result, NewsTab.class);


        String more = newsTab.data.more;
        if (!TextUtils.isEmpty(more)) {
            moreUrl = GlobalConstants.SERVER_URL + more;
        } else {
            moreUrl = null;
        }

        if (!isMore) {
            //初始化头条新闻数据
            topNewsList = newsTab.data.topnews;
            if (topNewsList != null) {
                mViewPager.setAdapter(new TopNewsAdapter());

                mIndicator.setViewPager(mViewPager);//将圆形指示器和ViewPager绑定
                mIndicator.setSnap(true);//快照展示，不展示圆点移动效果
                mIndicator.onPageSelected(0);//将圆点位置归零

                //更新头条标题新闻
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        //更新头条标题新闻
                        tvTitle.setText(topNewsList.get(position).title);
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                //初始化头条新闻第一个页面默认标题
                tvTitle.setText(topNewsList.get(0).title);

                //启动头条新闻的图片轮播
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            int currentItem = mViewPager.getCurrentItem();
                            if (currentItem < topNewsList.size() - 1) {
                                currentItem++;
                            } else {
                                currentItem = 0;//如果已经是最后一页，重新从第一页开始
                            }
                            mViewPager.setCurrentItem(currentItem);
                            mHandler.sendEmptyMessageDelayed(0, 2000);
                        }
                    };
                    //发送延时消息，启动自动轮播
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                }
                //设置触摸事件。按住头条新闻和抬起时事件
                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                //按下时。移除消息，停止轮播
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_CANCEL://事件取消。当按住新闻头条后，突然上下滑动ListView，导致当前ViewPager事件被取消，而不响应抬起事件
                            case MotionEvent.ACTION_UP://抬起
                                mHandler.sendEmptyMessageDelayed(0, 2000);//发送延时消息，启动自动轮播
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }

            newsList = newsTab.data.news;
            if (newsList != null) {
                newsAdapter = new NewsAdapter();
                lvList.setAdapter(newsAdapter);
            }
        } else {
            //追加更多
            ArrayList<NewsTab.News> moreNews = newsTab.data.news;
            newsList.addAll(moreNews);//追加更多数据
            newsAdapter.notifyDataSetChanged();//刷新
        }

    }

    //头条新闻的数据适配器
    class TopNewsAdapter extends PagerAdapter {
        private BitmapUtils bitmapUtils;

        public TopNewsAdapter() {
            bitmapUtils = new BitmapUtils(mActivity);
            //设置默认加载图片
            bitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return topNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            NewsTab.TopNews topNews = topNewsList.get(position);
            String topimage = topNews.topimage;//图片下载链接

            view.setScaleType(ImageView.ScaleType.FIT_XY);//设置缩放模式，宽高适配窗体

            bitmapUtils.display(view, topimage);//做图片缓存

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //新闻列表数据适配器
    class NewsAdapter extends BaseAdapter {
        private BitmapUtils bitmapUtils;

        public NewsAdapter() {
            bitmapUtils = new BitmapUtils(mActivity);
            bitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public NewsTab.News getItem(int i) {
            return newsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHolder();
                holder.ivIcon = view.findViewById(R.id.iv_icon);
                holder.ivTitle = view.findViewById(R.id.iv_title);
                holder.ivTime = view.findViewById(R.id.iv_time);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            NewsTab.News news = getItem(i);
            holder.ivTitle.setText(news.title);
            holder.ivTime.setText(news.pubdate);
            bitmapUtils.display(holder.ivIcon, news.listimage);

            String readIds = PrefUtils.getString(mActivity, "read_ids", "");
            if (readIds.contains(news.id)) {
                holder.ivTitle.setTextColor(Color.GRAY);
            } else {
                holder.ivTitle.setTextColor(Color.BLACK);
            }
            return view;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView ivTitle;
        public TextView ivTime;
    }


}
