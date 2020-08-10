package com.yrj.zhbj.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import com.yrj.zhbj.R;

/**
 * 5个标签页的基类
 * 5个标签页有的共性：子类都有标题栏，所以可以在父类中加载布局页面
 */
public class BasePager {

    public Activity mActivity;
    public TextView tvTitle;
    public ImageButton btnMenu;
    public FrameLayout flContainer;//空的帧布局，由子类来动态填充

    public View mRootView;//当前页面的根布局

    public BasePager(Activity activity) {
        mActivity = activity;
        // 在页面对象创建时就初始化了布局
        mRootView = initViews();
    }

    //初始化数据
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        tvTitle = view.findViewById(R.id.tv_title);
        btnMenu = view.findViewById(R.id.btn_menu);
        flContainer = view.findViewById(R.id.fl_container);
        return view;
    }

    //初始化数据
    public void initData() {

    }
}
