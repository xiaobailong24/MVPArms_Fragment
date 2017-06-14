package me.xiaobailong24.mvparmsfragment.mvp.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.UiUtils;

import butterknife.OnClick;
import me.xiaobailong24.mvparmsfragment.R;
import me.xiaobailong24.mvparmsfragment.app.EventBusTags;
import timber.log.Timber;

/**
 * Created by xiaobailong24 on 2017/6/12.
 * MVP HomeFragment
 */

public class HomeFragment extends BaseFragment {


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void setupFragmentComponent(AppComponent appComponent) {
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }


    @Override
    public void setData(Object data) {
        Message message = (Message) data;
        Timber.i("setData: message.what--->" + message.what);
        switch (message.what) {
            case EventBusTags.SETTING_FRAGMENT_HOME:
                UiUtils.makeText(getContext(), String.valueOf(message.what));
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //refresh
        }
    }

    @OnClick({R.id.navi_dashboard, R.id.navi_notifications})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.navi_dashboard:
                ARouter.getInstance()
                        .build(EventBusTags.AROUTER_PATH_MAIN)
                        .withInt(EventBusTags.ACTIVITY_FRAGMENT_REPLACE, EventBusTags.MAIN_FRAGMENT_DASHBOARD)
                        .navigation();
                break;
            case R.id.navi_notifications:
                ARouter.getInstance()
                        .build(EventBusTags.AROUTER_PATH_MAIN)
                        .withInt(EventBusTags.ACTIVITY_FRAGMENT_REPLACE, EventBusTags.MAIN_FRAGMENT_NOTIFICATIONS)
                        .navigation();
                break;
        }
    }
}