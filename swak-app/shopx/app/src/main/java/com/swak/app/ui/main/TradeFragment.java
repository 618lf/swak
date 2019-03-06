package com.swak.app.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.swak.app.R;
import com.swak.app.base.BaseFragment;
import com.swak.app.model.BannerBean;
import com.veni.tools.StatusBarTools;
import com.veni.tools.view.ShoppingView;
import com.veni.tools.view.TitleView;
import com.veni.tools.view.imageload.ImageLoaderTool;
import com.veni.tools.view.itoast.ToastTool;
import com.veni.tools.view.ticker.TickerUtils;
import com.veni.tools.view.ticker.TickerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TradeFragment extends BaseFragment {

    @BindView(R.id.trade_title_view)
    TitleView tradeTitleView;
    @BindView(R.id.trade_sv_1)
    ShoppingView tradeSv1;
    @BindView(R.id.trade_made_count)
    TickerView tradeMadeCount;
    @BindView(R.id.trade_refreshlayout)
    SmartRefreshLayout tradeRefreshlayout;
    @BindView(R.id.trade_banner)
    BGABanner tradeBanner;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_trade;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //增加状态栏的高度
        StatusBarTools.setPaddingSmart(context, tradeTitleView);
        //设置显示标题
        tradeTitleView.setTitle("添加购物车");

        //设置需要滚动的字符
        tradeMadeCount.setCharacterList(TickerUtils.getDefaultNumberList());
        tradeMadeCount.setText("￥0.0", true);
        //购物车控件点击监听
        tradeSv1.setOnShoppingClickListener(new ShoppingView.ShoppingClickListener() {
            @Override
            public void onAddClick(int num) {
                tradeMadeCount.setText("数量："+num, true);
            }

            @Override
            public void onMinusClick(int num) {
                tradeMadeCount.setText("数量："+num, false);
            }
        });

        //SmartRefreshLayout 刷新加载监听
        tradeRefreshlayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                clooserefreshlayout();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshlayout) {
                clooserefreshlayout();
            }
        });
        //SmartRefreshLayout 刷新加载Header样式
        tradeRefreshlayout.setRefreshHeader(new ClassicsHeader(context));

        tradeBanner.setAdapter(new BGABanner.Adapter<ImageView, BannerBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable BannerBean model, int position) {
                if(model!=null){
                    ImageLoaderTool.with(context).loadUrl(model.getBanner_url()).into(itemView);
                }
            }
        });
        tradeBanner.setDelegate(new BGABanner.Delegate<ImageView, BannerBean>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, @Nullable BannerBean model, int position) {

            }
        });
        List<BannerBean> data =new ArrayList<>();
        data.add(new BannerBean("http://a0.att.hudong.com/31/35/300533991095135084358827466.jpg"));
        data.add(new BannerBean("http://a3.topitme.com/1/21/79/1128833621e7779211o.jpg"));
        data.add(new BannerBean("http://x.itunes123.com/uploadfiles/1b13c3044431fb712bb712da97f42a2d.jpg"));
        data.add(new BannerBean("http://x.itunes123.com/uploadfiles/a3864382d68ce93bb7ab84775cb12d17.jpg"));
        data.add(new BannerBean("http://c.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81924a92c5b13eb13533fa4099.jpg"));
        List<String> tiplist = new ArrayList<>();
        for(BannerBean homeBannerBean:data){
            tiplist.add(homeBannerBean.getTips());
        }
        tradeBanner.setData(data, tiplist);
    }


    @OnClick({R.id.trade_made_count})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.trade_made_count:
                tradeSv1.setTextNum(1);
                break;
        }
    }

    /**
     * 模拟刷新加载
     */
    private void clooserefreshlayout() {
        mRxManager.add(Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        tradeMadeCount.setText("￥0.0", true);
                        tradeSv1.setTextNum(0);
                        tradeRefreshlayout.finishRefresh();
                        tradeRefreshlayout.finishLoadMore();
                    }
                }));

    }
}
