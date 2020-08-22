package com.yrj.zhbj.utils.bitmap;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * 内存缓存工具类
 */
public class MemoryCacheUtils {
    //把数据存在map中
    private HashMap<String, Bitmap> hashMap = new HashMap<>();

    //写缓存
    public void setMemoryCache(String url, Bitmap bitmap) {
        hashMap.put(url, bitmap);
    }

    //读缓存
    public Bitmap getMemoryCache(String url) {
        Bitmap bitmap = hashMap.get(url);
        return bitmap;
    }
}
