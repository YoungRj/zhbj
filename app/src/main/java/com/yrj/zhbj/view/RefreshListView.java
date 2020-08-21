package com.yrj.zhbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.yrj.zhbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 下拉刷新和上滑刷新ListView
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {

    private View mHeadView;
    private int measuredHeight;
    private View mFooterView;//脚布局
    private int footerMeasuredHeight;

    private static final int STATE_PULL_TO_REFRESH = 0;//下拉刷新
    private static final int STATE_RELEASE_TO_REFRESH = 1;//松开刷新
    private static final int STATE_REFRESHING = 2;//正在刷新

    private int mCurrentState = STATE_PULL_TO_REFRESH;//当前状态，默认下拉刷新

    private TextView tvState;
    private TextView tvTime;
    private ImageView ivArrow;
    private ProgressBar pbLoading;
    private RotateAnimation animUp;
    private RotateAnimation animDown;

    private boolean isLoadMore = false;//是否加载更多

    public RefreshListView(Context context) {
        super(context);
        initHeaderView();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initHeaderView();
    }

    //初始化头布局
    private void initHeaderView() {
        mHeadView = View.inflate(getContext(), R.layout.pull_to_refresh_head, null);
        addHeaderView(mHeadView);//给ListView添加头布局

        tvState = mHeadView.findViewById(R.id.tv_state);
        tvTime = mHeadView.findViewById(R.id.tv_time);
        ivArrow = mHeadView.findViewById(R.id.iv_arrow);
        pbLoading = mHeadView.findViewById(R.id.pb_loading);

        //隐藏头布局
        //获取当前头布局高度，然后设置paddingTop为负，布局就会向上走
//      int height = mHeadView.getHeight();//这样拿不到高度，因为控件没有绘制完成
        mHeadView.measure(0, 0);//手动测量，宽高为0表示不参与具体宽高的设定，全由系统设定

        measuredHeight = mHeadView.getMeasuredHeight();
        mHeadView.setPadding(0, -measuredHeight, 0, 0);//隐藏下拉刷新控件

        initArrowAnim();
        setRefreshTime();//设置时间
    }

    //初始化脚布局
    public void initFooterView() {
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        addFooterView(mFooterView);
        mFooterView.measure(0, 0);
        footerMeasuredHeight = mFooterView.getMeasuredHeight();
        //隐藏脚布局
        mFooterView.setPadding(0, -footerMeasuredHeight, 0, 0);

        setOnScrollListener(this);
    }

    private int startY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                if (startY == -1) {//没有获取到按下的事件（按住头条新闻滑动时，按下事件被ViewPager消费了）
                    startY = (int) ev.getY();//重新获取起点位置
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;

                //如果正在刷新，就什么都不做
                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }

                int firstVisiblePosition = this.getFirstVisiblePosition();//当前显示的第一个Item位置
                //下拉动作&当前在ListView的顶部
                if (dy > 0 && firstVisiblePosition == 0) {
                    int padding = -measuredHeight + dy;

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        //切换到松开刷新状态
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding <= 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        //切换到下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }

                    mHeadView.setPadding(0, padding, 0, 0);
                    return true;//消费此事件，处理下拉刷新控件的滑动，不需要ListView原生修改参与
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起
                startY = -1;
                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    //切换至正在刷新
                    mCurrentState = STATE_REFRESHING;
                    //正在刷新时，保持住正在刷新的高度
                    mHeadView.setPadding(0, 0, 0, 0);
                    refreshState();
                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    //隐藏下拉刷新控件
                    mHeadView.setPadding(0, -measuredHeight, 0, 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);//要返回super，方便ListView原生的处理事件
    }

    //箭头的动画
    private void initArrowAnim() {
        //箭头向上
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(300);//300ms
        animUp.setFillAfter(true);//保持住动画结束的状态

        //箭头向下
        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(300);//300ms
        animUp.setFillAfter(true);//保持住动画结束的状态
    }

    //根据当前状态刷新界面
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvState.setText("下拉刷新");
                pbLoading.setVisibility(View.INVISIBLE);//隐藏正在刷新
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.setAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvState.setText("松开刷新");
                pbLoading.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.setAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvState.setText("正在刷新...");
                pbLoading.setVisibility(View.VISIBLE);

                ivArrow.clearAnimation();//先清理之前的动画才能隐藏
                ivArrow.setVisibility(View.INVISIBLE);//隐藏箭头

                //回调下拉刷新
                if (mListener != null) {
                    mListener.onRefresh();
                }
                break;
            default:
                break;
        }
    }

    //隐藏控件
    public void onRefreshComplete() {
        if (!isLoadMore) {
            mHeadView.setPadding(0, -measuredHeight, 0, 0);
            setRefreshTime();
            //初始化数据
            tvState.setText("下拉刷新");
            pbLoading.setVisibility(View.INVISIBLE);//隐藏正在刷新
            ivArrow.setVisibility(View.VISIBLE);
            mCurrentState = STATE_PULL_TO_REFRESH;
        } else {
            mFooterView.setPadding(0, -footerMeasuredHeight, 0, 0);
            isLoadMore = false;
        }

    }

    private OnRefreshListener mListener;

    /* 设置刷新回调监听 */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    //回调接口，通知刷新状态
    public interface OnRefreshListener {
        //下拉刷新的回调
        public void onRefresh();

        //加载更多
        public void onLoadMore();
    }

    //刷新时间
    public void setRefreshTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        tvTime.setText(time);
    }

    //滑动状态发生变化
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        //空闲状态
        if (i == SCROLL_STATE_IDLE) {
            int lastVisiblePosition = getLastVisiblePosition();//当前显示的item最后一个位置
            if (lastVisiblePosition == getCount() - 1 && !isLoadMore) {//滑动到底部
                isLoadMore = true;

                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(getCount() - 1);

                //加载更多
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }

        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
