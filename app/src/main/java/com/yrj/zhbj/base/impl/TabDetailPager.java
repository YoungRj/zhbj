package com.yrj.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.Cache;
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
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.domain.NewsMenu;
import com.yrj.zhbj.domain.NewsTab;
import com.yrj.zhbj.global.GlobalConstants;
import com.yrj.zhbj.utils.CacheUtils;
import org.jetbrains.annotations.NotNull;

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
    private ViewPager mViewPager;

    private String mUrl;
    private ArrayList<NewsTab.TopNews> topNewsList;

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
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
//        view.setText(newsTabData.title);
        String cache = CacheUtils.getCache(mActivity, mUrl, null);
        if(!TextUtils.isEmpty(cache)){
            processData(cache);
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
                processData(result);
                CacheUtils.setCache(mActivity,mUrl,result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //解析数据
    private void processData(String result) {
        Gson gson = new Gson();
        NewsTab newsTab = gson.fromJson(result, NewsTab.class);
        //初始化头条新闻数据
        topNewsList = newsTab.data.topnews;
        if(topNewsList != null ){
            mViewPager.setAdapter(new TopNewsAdapter());
        }

    }

    //头条新闻的数据适配器
    class TopNewsAdapter extends PagerAdapter {
        private BitmapUtils bitmapUtils;
        public TopNewsAdapter(){
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
}
