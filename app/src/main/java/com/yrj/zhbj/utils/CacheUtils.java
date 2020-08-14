package com.yrj.zhbj.utils;

import android.content.Context;

/**
 * 缓存Utils
 */
public class CacheUtils {

    //写缓存，以url做key
    public static void setCache(Context ctx, String url, String value){
        //有时也会以MD5(url)做文件名存储
        PrefUtils.setString(ctx, url, value);
    }

    //读缓存
    public static String getCache(Context ctx, String url, String value){
        return PrefUtils.getString(ctx, url, null);
    }
}
