package com.yrj.zhbj.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yrj.zhbj.MainActivity;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BasePager;
import com.yrj.zhbj.base.impl.*;
import com.yrj.zhbj.view.NoScrollViewPager;

import java.util.ArrayList;

/**
 * 主页Fragment
 */
public class ContentFragment extends BaseFragment {

    @ViewInject(R.id.vp_content)
    private NoScrollViewPager mViewPager;
    @ViewInject(R.id.rg_group)
    private RadioGroup rgGroup;

    private ArrayList<BasePager> mList;//5个标签的集合

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
//        mViewPager = view.findViewById(R.id.vp_content);
        ViewUtils.inject(this, view);//使用此XUtils控件实现findViewById
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

        //点击低栏标签切换页面
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rg_home:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case R.id.rg_news:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rg_smart:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rg_gov:
                        mViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.rg_setting:
                        mViewPager.setCurrentItem(4, false);
                        break;
                    default:
                        break;
                }
            }
        });

        //监听ViewPager事件，初始化当前页面数据，点哪个页面再初始化哪个页面的数据
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //点哪个页面就初始化哪个页面的数据
                BasePager pager = mList.get(position);
                pager.initData();
                //
                if(position ==0 || position == mList.size()-1){
                    //禁用侧边栏
                    setSlidingMenuEnable(false);
                }else{
                    //开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //手动初始化第一个页面数据
        mList.get(0).initData();
        setSlidingMenuEnable(false);//禁用侧边栏
    }

    // 开启或禁用侧边栏
    public void setSlidingMenuEnable(boolean enable) {
        //获取MainActivity对象，因为继承了BaseFragment，而在MainActivity中使用当前Fragment替换（replace）
        //通过MainActivity获取SlidingMenu对象
        MainActivity mainActivity = (MainActivity) getActivity();
        SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
        if (enable) {
            //开启
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else{
            //禁用
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class ContentAdapter extends PagerAdapter {

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
            //此处初始化数据会导致启动程序时5个页面都会初始化，这样会浪费资源
//            pager.initData();//初始化数据

            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //获取新闻中心对象
    public NewsCenterPager getNewsCenterPager(){
        NewsCenterPager ncPager = (NewsCenterPager) mList.get(1);
        return ncPager;
    }
}
