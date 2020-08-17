package com.yrj.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/**
 * 解决：指示器请求所有父控件不拦截事件
 * 由于（TabDetailPager）滑动时出现了父类ViewPager拦截了子ViewPager的情况
 * 解决办法：自定义一个ViewPager继承ViewPager,重写它的dispatchTouchEvent方法
 */
public class TopNewsViewPager extends ViewPager {

    private int startX;
    private int startY;

    public TopNewsViewPager(Context context, AttributeSet arrs) {
        super(context, arrs);
    }

    public TopNewsViewPager(Context context) {
        super(context);
    }

    //1.上下滑动，需要拦截
    //2.左滑动，最后一个页面需要拦截
    //3.右滑动，第一个页面需要拦截
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //请求父控件不拦截事件
        getParent().requestDisallowInterceptTouchEvent(true);//父类不再拦截子类的事件
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();

                int dx = endX - startX;
                int dy = endY - startY;//移动距离
                //左右滑动。绝对值
                if (Math.abs(dx) > Math.abs(dy)) {

                    int currentItem = getCurrentItem();
                    if (dx > 0) {
                        //右滑，如果是第一个页面，需要拦截
                        if (currentItem == 0) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        //左滑,最后一个页面，需要拦截
                        int count = getAdapter().getCount();//当前ViewPager的Item总数
                        if (currentItem == count - 1) {
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                } else {
                    //上下滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }


        return super.dispatchTouchEvent(ev);
    }
}
