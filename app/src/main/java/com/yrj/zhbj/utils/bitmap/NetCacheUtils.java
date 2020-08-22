package com.yrj.zhbj.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存工具类
 */
public class NetCacheUtils {

    private ImageView imageView;
    private MemoryCacheUtils memoryCacheUtils;
    private LocalCacheUtils localCacheUtils;

    public NetCacheUtils(MemoryCacheUtils memoryCacheUtils, LocalCacheUtils localCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
        this.localCacheUtils = localCacheUtils;
    }

    public void getBitmapFromNet(ImageView imageView, String url) {
        new BitmapTask().execute(imageView, url);
    }

    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Object... objects) {
            imageView = (ImageView) objects[0];
            url = (String) objects[1];

            imageView.setTag(url);//给imageView打标签

            Bitmap bitmap = download(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                //给imageView设置图片
                //由于ListView的重用机制，导致某个Item有可能展示它所重用的Item那个图片，至图片混乱
                //解决方案：确保当前设置的图片和当前显示的imageView完全匹配
                String tempUrl = (String) imageView.getTag();
                if (this.url.equals(tempUrl)) {
                    imageView.setImageBitmap(bitmap);
                    System.out.println("从网络下载图片了");

                    //当从网络缓存下载数据后，保存到本地缓存，下次直接从本地缓存读取数据
                    localCacheUtils.setLocalCache(url, bitmap);
                    //当从网络缓存下载数据后，保存到内存缓存，下次直接从内存缓存读取数据
                    memoryCacheUtils.setMemoryCache(url, bitmap);
                }

            }
        }
    }

    //下载图片
    private Bitmap download(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                //使用输入流生成Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != null) {
                conn.disconnect();
            }
        }
        return null;
    }


}
