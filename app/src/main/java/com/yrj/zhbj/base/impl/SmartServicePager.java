package com.yrj.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import com.yrj.zhbj.base.BasePager;

public class SmartServicePager extends BasePager {

    public SmartServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给空的帧布局动态添加布局
        TextView view = new TextView(mActivity);
        view.setText("智慧服务");
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示

        flContainer.addView(view);//给帧布局添加view对象

        tvTitle.setText("生活");
    }
}
