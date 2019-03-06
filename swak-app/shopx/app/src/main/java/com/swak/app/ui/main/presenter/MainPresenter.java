package com.swak.app.ui.main.presenter;


import com.swak.app.R;
import com.swak.app.api.HttpManager;
import com.swak.app.api.HttpRespose;
import com.swak.app.api.RxSubscriber;
import com.swak.app.model.PersonalBean;
import com.swak.app.ui.main.contract.MainContract;
import com.veni.tools.baserx.RxSchedulers;

import java.util.HashMap;
import java.util.List;

/**
 * 作者：Administrator on 2017/12/04 10:36
 * 当前类注释:
 * Presenter
 */
public class MainPresenter extends MainContract.Presenter {

    @Override
    public void checkVersion(String type) {
        //请求参数
        HashMap<String, String> param = new HashMap<>();
        param.put("type", type);
        HttpManager.getInstance().getOkHttpUrlService().getLastVersion(param)
                .compose(RxSchedulers.<HttpRespose<List<PersonalBean>>>io_main())
                .subscribe(new RxSubscriber<List<PersonalBean>>(mContext, mContext.getString(R.string.loading)) {
                    @Override
                    public void _onNext(List<PersonalBean> data) {
                        mView.returnVersionData(data);
                    }

                    @Override
                    public void onErrorSuccess(int code, String message, boolean issuccess) {
                        mView.onErrorSuccess(code, message, issuccess,true);
                    }

                });
    }
}
