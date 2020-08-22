package com.yrj.zhbj.utils.bitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 自定义三级缓存工具类
 * 内存缓存->本地缓存->网络缓存，
 */
public class MyBitmapUtils {

    private MemoryCacheUtils memoryCacheUtils;//内存缓存，只有运行时才有效
    private LocalCacheUtils localCacheUtils;//本地缓存
    private NetCacheUtils netCacheUtils;//网络缓存

    public MyBitmapUtils(Activity activity) {
        memoryCacheUtils = new MemoryCacheUtils();
        localCacheUtils = new LocalCacheUtils(activity);
        netCacheUtils = new NetCacheUtils(memoryCacheUtils, localCacheUtils);
    }

    public void display(ImageView imageView, String url) {
        //内存缓存：速度很快，不浪费流量，优先
        Bitmap bitmap = memoryCacheUtils.getMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从内存缓存读取数据了");
            return;
        }

        //本地缓存：速度快，不浪费流量，其次
        bitmap = localCacheUtils.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从本地缓存读数据了。。。");
            memoryCacheUtils.setMemoryCache(url, bitmap);//从内存中读到数据后写入内存缓存
            return;//本地存在缓存，就不从网络缓存取数据
        }

        //网络缓存：速度慢，浪费流量，最后
        netCacheUtils.getBitmapFromNet(imageView, url);
    }

}
