package com.yrj.zhbj.view;

import android.content.Context;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/**
 * 解决：指示器请求所有父控件不拦截事件
 * 由于（TabDetailPager）滑动时出现了父类ViewPager拦截了子ViewPager的情况
 * 解决办法：自定义一个ViewPager继承ViewPager,重写它的dispatchTouchEvent方法
 */
public class HorizontalViewPage extends ViewPager {
    public HorizontalViewPage(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//父类不再拦截子类的事件
        return super.dispatchTouchEvent(ev);
    }
}
