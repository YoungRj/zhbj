package com.yrj.zhbj;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import android.widget.*;
import android.os.Bundle;
import cn.sharesdk.onekeyshare.OnekeyShare;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mob.MobSDK;
import com.mob.OperationCallback;

public class NewsDetailActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.tv_title)
    private TextView tvTitle;
    @ViewInject(R.id.btn_menu)
    private ImageButton btnMenu;
    @ViewInject(R.id.btn_back)
    private ImageButton btnBack;
    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;
    @ViewInject(R.id.btn_textSize)
    private ImageButton btnTextSize;
    @ViewInject(R.id.btn_share)
    private ImageButton btnShare;
    @ViewInject(R.id.webview)
    private WebView mWebView;
    @ViewInject(R.id.pb_loading)
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ViewUtils.inject(this);

        initViews();

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//设置加载js功能
        settings.setUseWideViewPort(true);//设置双击缩放(对做了适配的网页失效)

        //给WebView设置监听
        mWebView.setWebViewClient(new WebViewClient() {
            //网页开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbLoading.setVisibility(View.VISIBLE);
            }

            //跳转链接
//            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //所有跳转链接强制在当前webview加载，不跳浏览器
                System.out.println("跳转链接：" + request.getUrl().toString());
                mWebView.loadUrl(request.getUrl().toString());
                return true;
            }

            //加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            //获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                System.out.println("网页标题：" + title);
            }

            //进度发生变化
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                System.out.println("加载进度：" + newProgress);
            }
        });

        String url = getIntent().getStringExtra("url");
        //开始加载网页
        mWebView.loadUrl(url);
    }

    private void initViews() {
        btnMenu.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        llControl.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {//判断是否可以返回
            mWebView.goBack();//返回上一页
        } else {
            finish();
        }
//        mWebView.goForward();//跳转到下一网页页(前提是之前浏览过，有历史记录）
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_textSize:
                showChooseDialog();//显示修改字体弹窗
                break;
            case R.id.btn_share:
                showShare();//显示分享弹窗
                break;
            default:
                break;
        }
    }

    //shareSDK分享
    private void showShare() {
        MobSDK.submitPolicyGrantResult(true, null);
        OnekeyShare oks = new OnekeyShare();
        // title标题，微信、QQ和QQ空间等平台使用
//        oks.setTitle(getString(R.string.share));
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // setImageUrl是网络图片的url
        oks.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
        // url在微信、Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(MobSDK.getContext());
    }

    private int mTempWhich;//临时选中位置
    private int mCurrentWhich = 2;//当前选中位置

    //显示修改字体弹窗
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体设置");

        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
        //显示单选弹窗，参1：单选字符串数组，参2：当前选中位置，参3：选中监听
        builder.setSingleChoiceItems(items, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                System.out.println("当前选中位置：" + which);
                mTempWhich = which;
            }
        });

        //确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich) {
                    case 0:
                        settings.setTextZoom(200);
                        break;
                    case 1:
                        settings.setTextZoom(150);
                        break;
                    case 2:
                        settings.setTextZoom(100);
                        break;
                    case 3:
                        settings.setTextZoom(75);
                        break;
                    case 4:
                        settings.setTextZoom(50);
                        break;
                    default:
                        break;
                }
                mCurrentWhich = mTempWhich;//更新选中位置
            }
        });

        //取消按钮
        builder.setNegativeButton("取消", null);

        builder.show();
    }
}