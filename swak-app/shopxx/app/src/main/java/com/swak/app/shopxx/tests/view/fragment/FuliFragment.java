package com.swak.app.shopxx.tests.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.swak.app.core.base.BaseFragment;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.core.tools.ImageTools;
import com.swak.app.shopxx.R;
import com.swak.app.shopxx.tests.bean.SplashBean;
import com.swak.app.shopxx.tests.contract.FuliContract;
import com.swak.app.shopxx.tests.presenter.FuliPresenter;

import java.util.List;
import java.util.Random;

import butterknife.BindView;

/**
 * 福利门
 */
public class FuliFragment extends BaseFragment<FuliPresenter> implements FuliContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private int PAGE_COUNT = 1;
    private BaseQuickAdapter<SplashBean, BaseViewHolder> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fuli;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAdapter = new BaseQuickAdapter<SplashBean, BaseViewHolder>(R.layout.fragment_fuli_item) {

            @Override
            protected void convert(BaseViewHolder helper, SplashBean item) {
                ImageView function_item_iv = helper.getView(R.id.girl_item_iv);
                ImageTools.load(getContext(), item.getUrl(), function_item_iv);
                //TextView function_item_tv = helper.getView(R.id.girl_item_tv);
               // function_item_tv.setText(item.getDesc());
            }
        });

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                PAGE_COUNT = 1;
                presenter.refresh();
            }

            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                presenter.loadMore(PAGE_COUNT++);
            }
        });

        // 初始加载数据
        presenter.refresh();
    }

    /**
     * 获取数据之后
     *
     * @param datas
     */
    @Override
    public void onRefreshSuccess(final List<SplashBean> datas) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.replaceData(datas);
                refreshLayout.finishRefresh();
                refreshLayout.resetNoMoreData();
                recyclerView.requestLayout();
            }
        }, 2000);
    }

    /**
     * 获取数据之后
     *
     * @param datas
     */
    @Override
    public void onPageSuccess(final List<SplashBean> datas) {
        refreshLayout.getLayout().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (datas == null || datas.size() == 0) {
                    Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    mAdapter.addData(datas);
                    refreshLayout.finishLoadMore();
                }
                recyclerView.requestLayout();
            }
        }, 1000);
    }

    /**
     * 获取数据失败
     *
     * @param e
     */
    @Override
    public void onPageError(ResponseException e) {
        Toast.makeText(getContext(), "数据加载错误", Toast.LENGTH_SHORT).show();
    }
}
