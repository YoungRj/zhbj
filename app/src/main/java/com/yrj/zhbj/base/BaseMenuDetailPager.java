package com.yrj.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * 菜单详情页基类，“新闻”“专题”“组图”“互动”
 */
public abstract class BaseMenuDetailPager {

    public Activity mActivity;
    public View mRootView;//菜单详情页的根布局

    public BaseMenuDetailPager(Activity activity){
        mActivity = activity;
        mRootView = initViews();
    }

    //必须由子类实现
    public abstract View initViews();

    public void initData(){

    }
}
