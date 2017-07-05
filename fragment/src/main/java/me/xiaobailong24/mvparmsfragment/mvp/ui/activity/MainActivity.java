package me.xiaobailong24.mvparmsfragment.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.base.delegate.IFragment;
import com.jess.arms.di.component.AppComponent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.xiaobailong24.mvparmsfragment.R;
import me.xiaobailong24.mvparmsfragment.app.EventBusTags;
import me.xiaobailong24.mvparmsfragment.mvp.ui.adapter.FragmentAdapter;
import timber.log.Timber;

import static me.xiaobailong24.mvparmsfragment.app.EventBusTags.ACTIVITY_FRAGMENT_REPLACE;

/**
 * Created by xiaobailong24 on 2017/6/12.
 * MVP MainActivity
 * Activity 控制 Fragment 界面逻辑
 */

@Route(path = EventBusTags.AROUTER_PATH_MAIN)
public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    private List<Integer> mTitles;
    private FragmentAdapter mFragmentAdapter;
    private List<Integer> mNavIds;
    private int mReplace = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                mReplace = 0;
                break;
            case R.id.navigation_dashboard:
                mReplace = 1;
                break;
            case R.id.navigation_notifications:
                mReplace = 2;
                break;
        }
        mToolbarTitle.setText(mTitles.get(mReplace));
        getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.main_frame, mFragmentAdapter.getItem(mReplace))
                .commit();
        return true;
    };


    @Override
    public void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        this.setTitle(R.string.title_home);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (mTitles == null) {
            mTitles = new ArrayList<>();
            mTitles.add(R.string.title_home);
            mTitles.add(R.string.title_dashboard);
            mTitles.add(R.string.title_notifications);
        }
        if (mNavIds == null) {
            mNavIds = new ArrayList<>();
            mNavIds.add(R.id.navigation_home);
            mNavIds.add(R.id.navigation_dashboard);
            mNavIds.add(R.id.navigation_notifications);
        }
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        for (int i = 0; i < mFragmentAdapter.getCount(); i++)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, mFragmentAdapter.getItem(i))
                    .commit();
        mNavigation.setSelectedItemId(mNavIds.get(mReplace));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Fragment与Activity通信，使用ARouter。
        // 在AndroidManifest.xml中将Activity启动模式设为：launchMode="singleTask"
        mReplace = intent.getIntExtra(ACTIVITY_FRAGMENT_REPLACE,
                EventBusTags.MAIN_FRAGMENT_HOME) - EventBusTags.MAIN_FRAGMENT_HOME;
        Timber.d("onNewIntent: mReplace--->" + mReplace);
        mNavigation.setSelectedItemId(mNavIds.get(mReplace));
    }

    @Override
    public void onBackPressed() {
        if (mReplace != 0) {
            mReplace = 0;
            mNavigation.setSelectedItemId(R.id.navigation_home);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Message message = new Message();
            IFragment fragment = (IFragment) mFragmentAdapter.getItem(mReplace);
            switch (mReplace) {
                case 0:
                    message.what = EventBusTags.SETTING_FRAGMENT_HOME;
                    break;
                case 1:
                    message.what = EventBusTags.SETTING_FRAGMENT_DASHBOARD;
                    break;
                case 2:
                    message.what = EventBusTags.SETTING_FRAGMENT_NOTIFICATIONS;
                    break;
                default:
                    break;
            }
            //Activity与Fragment通信，使用Message
            fragment.setData(message);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存当前Activity显示的Fragment索引
        outState.putInt(ACTIVITY_FRAGMENT_REPLACE, mReplace);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mTitles = null;
        this.mFragmentAdapter = null;
        this.mNavIds = null;
    }
}
