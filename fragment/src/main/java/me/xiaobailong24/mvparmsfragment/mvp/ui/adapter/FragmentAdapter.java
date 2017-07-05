package me.xiaobailong24.mvparmsfragment.mvp.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.xiaobailong24.mvparmsfragment.mvp.ui.fragment.DashboardFragment;
import me.xiaobailong24.mvparmsfragment.mvp.ui.fragment.HomeFragment;
import me.xiaobailong24.mvparmsfragment.mvp.ui.fragment.NotificationsFragment;

/**
 * Created by xiaobailong24 on 2017/7/5.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"Home", "Dashboard", "Notifications"};

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                //建议用 newInstance 获得 Fragment， 换成 new DashboardFragment();也没问题
                return DashboardFragment.newInstance();
            case 2:
                return NotificationsFragment.newInstance();
        }
        return HomeFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
