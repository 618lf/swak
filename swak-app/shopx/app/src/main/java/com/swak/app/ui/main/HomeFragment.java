package com.swak.app.ui.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.swak.app.R;
import com.swak.app.base.BaseFragment;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.TitleView;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_rxtitle)
    TitleView homeRxtitle;
    @BindView(R.id.home_functions)
    RecyclerView homeFunctions;
    @BindView(R.id.home_refreshlayout)
    SmartRefreshLayout homeRefreshlayout;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        // 增加状态栏的高度
        StatusBarTools.setPaddingSmart(context, homeRxtitle);
    }
}
