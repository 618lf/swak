package com.veni.tools.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:基类fragment
 */
public abstract class FragmentBase extends Fragment {

    protected View rootView;
    public Context context;
    private AlertDialogBuilder dialogBuilder = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭Dialog
        destroyDialogBuilder();
    }

    /**
     * 获取默认Dialog
     */
    public AlertDialogBuilder creatDialogBuilder() {
        destroyDialogBuilder();
        dialogBuilder = new AlertDialogBuilder(context);
        return dialogBuilder;
    }

    public void destroyDialogBuilder() {
        if (dialogBuilder != null) {
            dialogBuilder.dismissDialog();
            dialogBuilder = null;
        }
    }

}
