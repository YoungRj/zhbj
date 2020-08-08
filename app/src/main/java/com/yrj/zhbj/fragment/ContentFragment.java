package com.yrj.zhbj.fragment;

import android.view.View;
import com.yrj.zhbj.R;

/**
 * 主页Fragment
 */
public class ContentFragment extends BaseFragment{
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        return view;
    }
}
