package com.yrj.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yrj.zhbj.R;
import com.yrj.zhbj.base.BaseMenuDetailPager;
import com.yrj.zhbj.domain.PhotosBen;
import com.yrj.zhbj.global.GlobalConstants;
import com.yrj.zhbj.utils.CacheUtils;
import com.yrj.zhbj.utils.bitmap.MyBitmapUtils;

import java.util.ArrayList;

/**
 * 菜单详情页--组图
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {

    @ViewInject(R.id.lv_list)
    private ListView lvList;
    @ViewInject(R.id.gv_list)
    private GridView gvList;

    private ImageButton btnDisplay;//组图中的ListView和GridView切换按钮

    private ArrayList<PhotosBen.PhotosNews> mNewsList;

    public PhotosMenuDetailPager(Activity activity, ImageButton btnDisplay) {
        super(activity);
        this.btnDisplay = btnDisplay;
        btnDisplay.setOnClickListener(this);
    }

    @Override
    public View initViews() {
//        TextView view = new TextView(mActivity);
//        view.setText("菜单详情页--组图");
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示

        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        ViewUtils.inject(this, view);

        return view;
    }

    public void initData() {
        String cache = CacheUtils.getCache(mActivity, GlobalConstants.PHOTOS_URL, null);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }

        getFromDataServer();
    }

    private void getFromDataServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);
                CacheUtils.setCache(mActivity, GlobalConstants.PHOTOS_URL, null);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBen photosBen = gson.fromJson(result, PhotosBen.class);
        mNewsList = photosBen.data.news;

        //给ListView设置适配器
        lvList.setAdapter(new PhotosAdapter());

        //给GridView设置适配器
        gvList.setAdapter(new PhotosAdapter());

    }

    private boolean isListView = true;//是否是ListView

    @Override
    public void onClick(View view) {
        System.out.println("isListView:" + isListView);
        if (isListView) {
            //显示GridView
            lvList.setVisibility(View.GONE);
            gvList.setVisibility(View.VISIBLE);
            btnDisplay.setImageResource(R.drawable.icon_pic_list_type);
            isListView = false;
        } else {
            //显示ListView
            lvList.setVisibility(View.VISIBLE);
            gvList.setVisibility(View.GONE);
            btnDisplay.setImageResource(R.drawable.icon_pic_grid_type);
            isListView = true;
        }
    }

    //组图适配器
    class PhotosAdapter extends BaseAdapter {
//        private BitmapUtils bitmapUtils;
        private MyBitmapUtils bitmapUtils;//使用自己定义的缓存

        public PhotosAdapter() {
//            bitmapUtils = new BitmapUtils(mActivity);
//            bitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);//设置默认图片
            bitmapUtils = new MyBitmapUtils(mActivity);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosBen.PhotosNews getItem(int i) {
            return mNewsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(mActivity, R.layout.list_item_photos, null);
                holder = new ViewHolder();
                holder.ivPic = view.findViewById(R.id.iv_pic);
                holder.tvTitle = view.findViewById(R.id.tv_title);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            PhotosBen.PhotosNews item = getItem(i);
            holder.tvTitle.setText(item.title);
            bitmapUtils.display(holder.ivPic, item.listimage);

            return view;
        }
    }

    static class ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
    }

}
