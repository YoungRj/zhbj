package com.yrj.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.yrj.zhbj.base.BaseMenuDetailPager;

/**
 * 菜单详情页--新闻
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {

    public NewsMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initViews() {
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页--新闻");
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示

        return view;
    }
}
