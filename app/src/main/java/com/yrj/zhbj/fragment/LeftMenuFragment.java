package com.yrj.zhbj.fragment;

import android.view.View;
import com.yrj.zhbj.R;

/**
 * 侧边栏Fragment，侧滑栏
 */
public class LeftMenuFragment extends BaseFragment {
    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
        return view;
    }
}
