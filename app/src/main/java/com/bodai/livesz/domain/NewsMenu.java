package com.bodai.livesz.domain;

import android.content.Intent;

import java.util.ArrayList;

/**
 * 分类信息封装
 * 使用gson解析时，遇见{}创建对象，遇见【】创建集合（ArrayList）,所有字段名称要和json返回字段高度一致
 * Created by daibo on 2016/9/20.
 */
public class NewsMenu {
    public int retcode;
    public ArrayList<Integer> extend;
    public ArrayList<NewsMenuData> data;

    //侧边栏对象
    public class NewsMenuData{
        public int id;
        public String title;
        public int type;
        public ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData{" +
                    "title='" + title + '\'' +
                    ", children=" + children +
                    '}';
        }
    }

    //页签对象
    public class NewsTabData{
        public int id;
        public String title;
        public int type;
        public String url;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    "title='" + title + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsMenu{" +
                "data=" + data +
                '}';
    }
}
