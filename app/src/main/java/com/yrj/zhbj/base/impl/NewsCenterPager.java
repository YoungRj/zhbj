package com.yrj.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yrj.zhbj.MainActivity;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.base.BasePager;
import com.yrj.zhbj.base.impl.menudetail.InteractMenuDetailPager;
import com.yrj.zhbj.base.impl.menudetail.NewsMenuDetailPager;
import com.yrj.zhbj.base.impl.menudetail.PhotosMenuDetailPager;
import com.yrj.zhbj.base.impl.menudetail.TopicMenuDetailPager;
import com.yrj.zhbj.domain.NewsMenu;
import com.yrj.zhbj.fragment.LeftMenuFragment;
import com.yrj.zhbj.global.GlobalConstants;

import java.util.ArrayList;

public class NewsCenterPager extends BasePager {

    private NewsMenu newsMenu;
    private ArrayList<BaseMenuDetailPager> mPagers;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻中心初始化了。。。。");
        //给空的帧布局动态添加布局
        /*TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示

        flContainer.addView(view);//给帧布局添加view对象*/

        tvTitle.setText("新闻");
        
        //从服务器获取数据
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                System.out.println("服务器数据："+result);

                parseData(result);

            }

            @Override
            public void onFailure(HttpException e, String msg) {
                e.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //解析json数据
    public void parseData(String json){
        Gson gson = new Gson();
        newsMenu  = gson.fromJson(json, NewsMenu.class);
        System.out.println("解析结果:" + newsMenu);

        MainActivity mainActivity = (MainActivity) mActivity;
        LeftMenuFragment lmFragment = mainActivity.getLeftMenuFragment();
        lmFragment.setMenuData(newsMenu.data);

        //网络请求成功后，初始化四个菜单详情页
        mPagers = new ArrayList<>();
        mPagers.add(new NewsMenuDetailPager(mActivity));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotosMenuDetailPager(mActivity));
        mPagers.add(new InteractMenuDetailPager(mActivity));

        //单击底部“新闻”默认显示第一个布局
        setMenuDetailPager(0);
    }

    //修改新闻中心菜单详情页
    public void setMenuDetailPager(int position) {
        System.out.println("要修改新闻中心详情页了。。。。"+position);

        BaseMenuDetailPager pager = mPagers.get(position);//获取点击对应菜单的对象

        //修改之前清楚之前帧布局显示的内容
        flContainer.removeAllViews();
        //修改当前帧布局显示的内容
        flContainer.addView(pager.mRootView);
        //初始化当前页面的数据
        pager.initData();

        tvTitle.setText(newsMenu.data.get(position).title);
    }
}
