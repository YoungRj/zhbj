package com.yrj.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import com.yrj.zhbj.R;

/**
 * 下拉刷新ListView
 */
public class RefreshListView extends ListView {

    private View mHeadView;

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initHeaderView();
    }

    private void initHeaderView() {
        mHeadView = View.inflate(getContext(), R.layout.pull_to_refresh_head, null);
        addHeaderView(mHeadView);//给ListView添加头布局
        //隐藏头布局
        //获取当前头布局高度，然后设置paddingTop为负，布局就会向上走
//      int height = mHeadView.getHeight();//这样拿不到高度，因为控件没有绘制完成
        mHeadView.measure(0, 0);//手动测量，宽高为0表示不参与具体宽高的设定，全由系统设定
        int measuredHeight = mHeadView.getMeasuredHeight();
//        mHeadView.setPadding(0, -measuredHeight, 0, 0);
    }
}
