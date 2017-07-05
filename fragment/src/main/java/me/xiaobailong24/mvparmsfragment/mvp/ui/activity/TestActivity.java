package me.xiaobailong24.mvparmsfragment.mvp.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

import butterknife.BindView;
import me.xiaobailong24.mvparmsfragment.R;
import me.xiaobailong24.mvparmsfragment.app.EventBusTags;

/**
 * Created by xiaobailong24 on 2017/7/5.
 * MVP TestActivity
 * Activity 在 xml 中使用 <fragment> 标签静态加载 Fragment
 */

@Route(path = EventBusTags.AROUTER_PATH_TEST)
public class TestActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_test;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mToolbarTitle.setText(R.string.title_activity_test);
    }

}
