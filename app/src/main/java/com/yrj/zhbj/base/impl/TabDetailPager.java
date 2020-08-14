package com.yrj.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.domain.NewsMenu;

/**
 * 页签详情页，“北京”“中国”“国际”
 * 继承BaseMenuDetailPager，从代码来讲比较简洁，但不符合面向对象
 * 但当前页不属于菜单详情页，这是个干爹
 */
public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData newsTabData;
    private TextView view;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        this.newsTabData = newsTabData;
    }

    @Override
    public View initViews() {
        view = new TextView(mActivity);
        view.setText("页签");
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示
        return view;
    }

    @Override
    public void initData() {
        view.setText(newsTabData.title);
    }
}
