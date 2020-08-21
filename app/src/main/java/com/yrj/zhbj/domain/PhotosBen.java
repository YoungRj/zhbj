package com.yrj.zhbj.domain;

import java.util.ArrayList;

/**
 * 组图
 */
public class PhotosBen {

    public PhotosData data;

    public class PhotosData{
        public ArrayList<PhotosNews> news;
    }

    public class PhotosNews{
        public String title;
        public String listimage;
    }
}
