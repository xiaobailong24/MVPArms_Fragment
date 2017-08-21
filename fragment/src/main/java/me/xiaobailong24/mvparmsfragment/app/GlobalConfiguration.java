package me.xiaobailong24.mvparmsfragment.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.App;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import me.xiaobailong24.mvparmsfragment.BuildConfig;
import me.xiaobailong24.mvparmsfragment.R;
import timber.log.Timber;

/**
 * app的全局配置信息在此配置,需要将此实现类声明到AndroidManifest中
 * Created by jess on 12/04/2017 17:25
 * Contact with jess.yan.effort@gmail.com
 */

public final class GlobalConfiguration implements ConfigModule {
    //    public static String sDomain = Api.APP_DOMAIN;

    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        if (!BuildConfig.LOG_DEBUG) //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        // AppLifecycles 的所有方法都会在基类Application对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        lifecycles.add(new AppLifecycles() {

            @Override
            public void attachBaseContext(Context base) {
                //                MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
            }

            @Override
            public void onCreate(Application application) {
                if (BuildConfig.LOG_DEBUG) {//Timber初始化
                    //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
                    //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
                    //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
                    Timber.plant(new Timber.DebugTree());
                    // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
                    //                    Logger.addLogAdapter(new AndroidLogAdapter());
                    //                    Timber.plant(new Timber.DebugTree() {
                    //                        @Override
                    //                        protected void log(int priority, String tag, String message, Throwable t) {
                    //                            Logger.log(priority, tag, message, t);
                    //                        }
                    //                    });
                }
                //leakCanary内存泄露检查
                ((App) application).getAppComponent().extras().put(RefWatcher.class.getName(), BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);

                //ARouter初始化
                if (BuildConfig.LOG_DEBUG) {
                    ARouter.openLog();     // 打印日志
                    ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
                }
                ARouter.init(application); // 尽可能早，推荐在Application中初始化
            }

            @Override
            public void onTerminate(Application application) {

            }
        });
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        lifecycles.add(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Timber.w(activity + " - onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Timber.w(activity + " - onActivityStarted");
                if (!activity.getIntent().getBooleanExtra("isInitToolbar", false)) {
                    //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
                    //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
                    activity.getIntent().putExtra("isInitToolbar", true);
                    //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
                    if (activity.findViewById(R.id.toolbar) != null) {
                        if (activity instanceof AppCompatActivity) {
                            ((AppCompatActivity) activity).setSupportActionBar((Toolbar) activity.findViewById(R.id.toolbar));
                            ((AppCompatActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                activity.setActionBar((android.widget.Toolbar) activity.findViewById(R.id.toolbar));
                                activity.getActionBar().setDisplayShowTitleEnabled(false);
                            }
                        }
                    }
                    if (activity.findViewById(R.id.toolbar_title) != null) {
                        ((TextView) activity.findViewById(R.id.toolbar_title)).setText(activity.getTitle());
                    }
                    if (activity.findViewById(R.id.toolbar_back) != null) {
                        activity.findViewById(R.id.toolbar_back).setOnClickListener(v -> {
                            activity.onBackPressed();
                        });
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Timber.w(activity + " - onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Timber.w(activity + " - onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Timber.w(activity + " - onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Timber.w(activity + " - onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Timber.w(activity + " - onActivityDestroyed");
            }
        });
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        lifecycles.add(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                // 在配置变化的时候将这个 Fragment 保存下来,在 Activity 由于配置变化重建是重复利用已经创建的Fragment。
                // https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean)
                // 如果在 XML 中使用 <Fragment/> 标签,的方式创建 Fragment 请务必在标签中加上 android:id 或者 android:tag 属性,否则 setRetainInstance(true) 无效
                // 在 Activity 中绑定少量的 Fragment 建议这样做,如果需要绑定较多的 Fragment 不建议设置此参数,如 ViewPager 需要展示较多 Fragment
                f.setRetainInstance(true);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                //这里应该是检测 Fragment 而不是 FragmentLifecycleCallbacks 的泄露。
                ((RefWatcher) ((App) f.getActivity()
                        .getApplication())
                        .getAppComponent()
                        .extras()
                        .get(RefWatcher.class.getName()))
                        .watch(f);
            }
        });
    }

}