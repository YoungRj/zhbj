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
}
