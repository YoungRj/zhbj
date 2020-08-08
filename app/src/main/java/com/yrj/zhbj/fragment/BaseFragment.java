package com.yrj.zhbj.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

/**
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    public Activity mActivity;//当做context去使用
    public View mRootView;//fragment的根布局

    //创建Fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();//获取fragment所依赖的对象
    }

    //初始化fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = initViews();
        return mRootView;
    }

    //fragment所在的activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //初始化布局，必须由子类来实现
    public abstract View initViews();

    //初始化数据
    public void initData() {

    }
}
