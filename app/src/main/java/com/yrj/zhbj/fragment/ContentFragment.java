package com.yrj.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BasePager;
import com.yrj.zhbj.base.impl.*;
import com.yrj.zhbj.view.NoScrollViewPager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * 主页Fragment
 */
public class ContentFragment extends BaseFragment{

    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mList;//5个标签的集合

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mViewPager = view.findViewById(R.id.vp_content);
        return view;
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        mList.add(new HomePager(mActivity));
        mList.add(new NewsCenterPager(mActivity));
        mList.add(new SmartServicePager(mActivity));
        mList.add(new GovAffairsPager(mActivity));
        mList.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());
    }

    class ContentAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取当前页面对象
            BasePager pager = mList.get(position);
            //布局对象
            //pager.mRootView当前页面的根布局
            container.addView(pager.mRootView);
            pager.initData();//初始化数据

            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
