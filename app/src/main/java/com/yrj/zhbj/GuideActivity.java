package com.yrj.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yrj.zhbj.utils.PrefUtils;

import java.util.ArrayList;

/**
 * 新手引导页
 */
public class GuideActivity extends Activity {

    private ViewPager viewPager;
    private LinearLayout llContainer;//线性布局,三个小圆点
    private ImageView ivRedPoint;//小红点
    private Button btnStart;//开始体验按钮
    // 图片ID集合
    private int[] mImageIds = new int[]{R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};

    //把图片数据存到集合中，初始化布局时取出来
    private ArrayList<ImageView> mImageViews;

    int mPointDis;// 小圆点之间的距离

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_guide);

        //初始化布局
        initViews();
        //初始化数据
        initData();
    }

    private void initViews() {
        viewPager = findViewById(R.id.vp_guide);
        llContainer = findViewById(R.id.ll_container);
        ivRedPoint = findViewById(R.id.iv_red_point);
        btnStart = findViewById(R.id.btn_start);
    }

    private void initData() {
        //初始化三张图片的ImageView
        mImageViews = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViews.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_normal);
            //初始化布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                params.leftMargin = 10;//从第二个圆点开始设置左边距10像素
            }
            point.setLayoutParams(params);//设置布局参数

            llContainer.addView(point);
        }
        viewPager.setAdapter(new GuideAdapter());
        //实现小红点移动
        //监听事件,监听ViewPage滑动事件，更新小红点位置
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //监听滑动事件
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 通过修改小红点的左边距来修改小红点的距离
                int leftMargin = (int) (mPointDis * positionOffset + mPointDis * position + 0.5f);//要把已经产生的距离加进来mPointDis * position
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin;
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if(position == mImageIds.length - 1) {
                    btnStart.setVisibility(View.VISIBLE);//显示开始体验
                } else{
                    btnStart.setVisibility(View.GONE);//隐藏开始体验
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //监听layout执行结束操作，一旦结束之后，就去获取当前的left位置
        //视图树
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            // 一旦视图树的layout调用完成，就会调用此方法
            @Override
            public void onGlobalLayout() {
                //圆点距离=第二个圆点左边距离-第一个圆点左边距离
                mPointDis = llContainer.getChildAt(1).getLeft() - llContainer.getChildAt(0).getLeft();
                //拿到距离就移除观察者
                ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //监听点击“开始体验”跳到主页
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在sp中记录访问过新手引导页的状态，下次打开程序就不用进入新手引导页
                PrefUtils.setBoolean(getApplicationContext(), "is_guide_show", true);
                //跳到主页
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViews.get(position);//从集合中取出图片数据
            container.addView(view);
            return view;
        }

        //销毁布局
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}