package com.veni.tools.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：kkan on 2017/12/07
 * 当前类注释:
 * TabLayout 配套的Adapter
 */

public class InnerPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles;

    public InnerPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    //刷新fragment
    public void setFragments(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        this.titles = titles;
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
        // super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
