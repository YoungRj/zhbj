package com.yrj.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.base.impl.TabDetailPager;
import com.yrj.zhbj.domain.NewsMenu;

import java.util.ArrayList;

/**
 * 菜单详情页--新闻
 *
 * 使用ViewPagerIndicator：
 * 1.引入ViewPagerIndicator库：
 *      ①在项目的build.gradle中添加：allprojects {repositories {maven { url "https://jitpack.io" }}}
 *      ②在模型（app）的build.gradle中添加：dependencies {compile 'com.github.JakeWharton:ViewPagerIndicator:2.4.1'}
 * 2.仿照smaple中的程序进行拷贝，SampleTabsDefault
 * 3.mIndicator.setViewPager(mViewPager);//将ViewPager和Indicator关联在一起
 * 4.重写PagerAdapter的getPageTitle返回指示器标题
 * 5.样式需要在AndroidManifest.xml中添加<activity android:name=".MainActivity"
 *                   android:theme="@style/Theme.ZHBJPageIndicatorDefaults">
 *         </activity> 样式根据需要更新
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    private ArrayList<NewsMenu.NewsTabData> children;
    private ArrayList<TabDetailPager> mPagers;//页签对象集合

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
        super(activity);
        this.children = children;
    }

    @Override
    public View initViews() {
//        TextView view = new TextView(mActivity);
//        view.setText("菜单详情页--新闻");
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData() {
        //初始化12个标签，以服务器为准
        mPagers = new ArrayList<>();
        for (int i = 0; i < children.size(); i++) {
            TabDetailPager pager = new TabDetailPager(mActivity, children.get(i));
            mPagers.add(pager);
        }

        mViewPager.setAdapter(new newsMenuDetailAdapter());
        //关联后，显示标题还需要在适配器里重写getPageTitle方法
        mIndicator.setViewPager(mViewPager);//将ViewPager和Indicator关联在一起。注意：必须设置setAdapter之后
    }

    class newsMenuDetailAdapter extends PagerAdapter {

        //返回指示器Indicator标题
        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);
            pager.initData();//初始化数据
            container.addView(pager.mRootView);
            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //点击跳转到下一个页签
    @OnClick(R.id.btn_next) //通过注解的方式绑定。注意：在xml配置中onClick属性只适用于activity
    public void nextPage(View view){
        int currentPos = mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(++currentPos);
    }
}
