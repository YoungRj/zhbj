package com.yrj.zhbj;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yrj.zhbj.base.impl.NewsCenterPager;
import com.yrj.zhbj.fragment.ContentFragment;
import com.yrj.zhbj.fragment.LeftMenuFragment;

/**
 * 主页面
 * SlidingActivity：侧滑菜单类
 * 1.当一个activity要展示Fragment的话，必须继承FragmentActivity
 * 2.使用FragmentManager布局进行替换
 * 3.将现有布局掏空，根布局建议使用FragmentLayout
 * 4.开始事务，进行替换操作，并提交
 */
public class MainActivity extends SlidingFragmentActivity {
    private static final String TAG_CONTENT = "TAG_CONTENT";//方便以后通过该标记找到对应fragment对象
    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //侧滑菜单，设置侧边栏布局
        setBehindContentView(R.layout.left_menu);

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏触摸
        slidingMenu.setBehindOffset(400);//预留210px

        initFragment();
    }

    //初始化布局
    public void initFragment() {
        //获取Fragment管理器
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();//开始一个事务
        //使用fragment替换现有布局，参1：当前布局的id，参2：要替换的fragment，
        // 参3：打一个标记，方便以后找到该fragment对象
        transaction.replace(R.id.fl_content, new ContentFragment(), TAG_CONTENT);
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);
        transaction.commit();//提交事务

        //通过tag找到该fragment
//        ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
    }

    //获取侧边栏对象
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment lmFragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
        return lmFragment;
    }

    //获取ContentPager对象
    public ContentFragment getContentFragment(){
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment ncFragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
        return ncFragment;
    }
}