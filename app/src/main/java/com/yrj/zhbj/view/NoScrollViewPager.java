package com.yrj.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/**
 * 禁止滑动ViewPager
 */
public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public NoScrollViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //重写父类的onTouchEvent，此处什么都不做，从而达到禁用事件的效果
        return true;
    }

    //事件中断，拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //true表示拦截；false表示不拦截，传给子控件
        return false;//标签页的ViewPager不拦截新闻菜单详情页页签的ViewPager
    }
}
