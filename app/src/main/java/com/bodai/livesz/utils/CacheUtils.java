package com.bodai.livesz.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 * Created by daibo on 2016/9/20.
 */
public class CacheUtils {
    /**
     * 以url为key，以json为value，保存在本地
     */
    public static void setCache(String url, String json, Context ctx){
        PrefUtils.setString(ctx,url,json);
    }
    //获取缓存
    public static String getCache(String url,Context ctx){
        return PrefUtils.getString(ctx,url,null);
    }
}
