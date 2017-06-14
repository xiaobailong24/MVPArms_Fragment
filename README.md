<<<<<<< HEAD
---
title: MVPArms 系列 -- Fragment 的正确使用
date: 2017-06-06 17:00:00
tags: [Android, MVPArms, Fragment]
categories: Android Blog
description: MVPArms 系列 -- Fragment 的正确使用
---
=======
# MVPArms 
[ ![Bintray](https://img.shields.io/badge/bintray-v2.1.0-brightgreen.svg) ](https://bintray.com/jessyancoding/maven/MVPArms/2.1.0/link)
[ ![Build Status](https://travis-ci.org/JessYanCoding/MVPArms.svg?branch=master) ](https://travis-ci.org/JessYanCoding/MVPArms)
[ ![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat-square) ](https://developer.android.com/about/versions/android-4.0.3.html)
[ ![License](http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square) ](http://www.apache.org/licenses/LICENSE-2.0)
[ ![QQGroup](https://img.shields.io/badge/QQ群-301733278-ff69b4.svg) ](https://shang.qq.com/wpa/qunwpa?idkey=1a5dc5e9b2e40a780522f46877ba243eeb64405d42398643d544d3eec6624917)
>>>>>>> master

# 前言
使用 [MVPArms](https://github.com/JessYanCoding/MVPArms) 开发也有一段时间了，首先感谢 [作者](https://github.com/JessYanCoding) 的无私奉献和分享！在此记录一下 **Fragment** 使用过程中遇到的问题和解决方案。

# 正文
## Activity/Fragment 生命周期
**Activity** 和 **Fragment** 的生命周期如下：

![Fragment的生命周期](https://developer.android.com/images/fragment_lifecycle.png?hl=zh-cn)

![Activity生命周期对片段生命周期的影响](https://developer.android.com/images/activity_fragment_lifecycle.png?hl=zh-cn)

![Github神图](https://github.com/xxv/android-lifecycle/raw/master/complete_android_fragment_lifecycle.png)


## MVPArms 对 Application/Activity/Fragment 的封装
**MVPArms** 框架对 **Activity** 和 **Fragment** 的生命周期进行了很好的封装。
通过 **ActivityDelegate** 代理 Activity 的生命周期(具体实现为 **ActivityDelegateImpl** )，通过 **FragmentDelegate** 代理 Fragment 的生命周期(具体实现为 **FragmentDelegateImpl** )；
然后在 **ActivityLifecycle** 实现了 **Application.ActivityLifecycleCallbacks** 接口，内部类 **FragmentLifecycle** 实现了 **FragmentManager.FragmentLifecycleCallbacks** 抽象类；
并将 ActivityLifecycle 注入到 **BaseApplication** 中，注入过程是通过 **AppDelegate** 来代理 **Application** 的生命周期完成的。
为此作者还专门通过 [一篇文章](http://www.jianshu.com/p/75a5c24174b2) 介绍思想，收获颇多。

- [ActivityDelegate](https://github.com/JessYanCoding/MVPArms/blob/master/arms/src/main/java/com/jess/arms/base/delegate/ActivityDelegate.java)

- [FragmentDelegate](https://github.com/JessYanCoding/MVPArms/blob/master/arms/src/main/java/com/jess/arms/base/delegate/FragmentDelegate.java)

- [ActivityLifecycle](https://github.com/JessYanCoding/MVPArms/blob/master/arms/src/main/java/com/jess/arms/integration/ActivityLifecycle.java)

- [BaseApplication](https://github.com/JessYanCoding/MVPArms/blob/master/arms/src/main/java/com/jess/arms/base/BaseApplication.java)

- [AppDelegate](https://github.com/JessYanCoding/MVPArms/blob/master/arms/src/main/java/com/jess/arms/base/delegate/AppDelegate.java)

[Application.ActivityLifecycleCallbacks](https://developer.android.com/reference/android/app/Application.ActivityLifecycleCallbacks.html) 接口定义如下：
```java
  public interface ActivityLifecycleCallbacks {
      void onActivityCreated(Activity activity, Bundle savedInstanceState);
      void onActivityStarted(Activity activity);
      void onActivityResumed(Activity activity);
      void onActivityPaused(Activity activity);
      void onActivityStopped(Activity activity);
      void onActivitySaveInstanceState(Activity activity, Bundle outState);
      void onActivityDestroyed(Activity activity);
  }
```

[FragmentManager.FragmentLifecycleCallbacks](https://developer.android.com/reference/android/support/v4/app/FragmentManager.FragmentLifecycleCallbacks.html) 抽象类定义如下：
```java
  public abstract static class FragmentLifecycleCallbacks {
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {}
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {}
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {}
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f,
                Bundle savedInstanceState) {}
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v,
                Bundle savedInstanceState) {}
        public void onFragmentStarted(FragmentManager fm, Fragment f) {}
        public void onFragmentResumed(FragmentManager fm, Fragment f) {}
        public void onFragmentPaused(FragmentManager fm, Fragment f) {}
        public void onFragmentStopped(FragmentManager fm, Fragment f) {}
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {}
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {}
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {}
        public void onFragmentDetached(FragmentManager fm, Fragment f) {}
  }
```

> 注意：ActivityLifecycleCallbacks 和 FragmentLifecycleCallbacks 里的方法是在 Activity 和 Fragment 对应生命周期的 super() 方法中进行的。即执行 super() 之后，先执行对应的 LifecycleCallbacks 里的方法，再执行 super() 之后的语句。


## Activity 控制 Fragment 的切换
### 场景描述
**MainActivity** 有三个 Fragment，分别是 **HomeFragment**，**DashboardFragment**，**NotificationsFragment**。
MainActivity 控制 Fragment 的切换，其中 HomeFragment 是主页。
### 解决方案(ARouter)
使用 [ARouter](https://github.com/alibaba/ARouter) 控制 Fragment 的切换。
#### 设置 Activity 启动模式
设置 MainActivity 的启动模式为 **singleTask**，在 **AndroidManifest.xml** 中为 MainActivity 添加以下属性：
```java
 android:launchMode="singleInstance"
```

> singleTask: singleTask 模式的 Activity 只允许在系统中有一个实例。
> 如果系统中已经有了一个实例，持有这个实例的任务将移动到顶部，同时 Intent 将被通过 **onNewIntent()** 发送。
> 如果没有，则会创建一个新的 Activity 并置放在合适的任务中。

#### 为 Activity 动态添加 Fragment
MainActivity 的布局文件包含了一个 FramLayout，用来动态添加 Fragment；还包含了一个 BottomNavigationView，在 Activity 中控制 Fragment 的切换。
- [activity_main.xml](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/res/layout/activity_main.xml)
- [MainActivity](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/activity/MainActivity.java)

MainActivity 中有一个 List 用来存储 Fragment，根据每个 Fragment 在 List 中的索引切换 Fragment。此处的切换方式使用的是 hide/show 的方式，当 Fragment 需要频繁切换的时候，这样做比 replace 的方式更有效率。

#### Fragment 之间的切换
每个 Fragment 的布局文件都有两个 Button，用来在一个 Fragment 切换至其他的 Fragment。
- [fragment_home.xml](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/res/layout/fragment_home.xml)
- [fragment_dashboard.xml](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/res/layout/fragment_dashboard.xml)
- [fragment_notifications.xml](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/res/layout/fragment_notifications.xml)

具体实现方式是由 ARouter 先跳转到 Activity，然后由 Activity 控制 Fragment 的切换。
 ```java
  ARouter.getInstance()
          .build(EventBusTags.AROUTER_PATH_MAIN) //Activity 的 ARouter 路径
          .withInt(EventBusTags.ACTIVITY_FRAGMENT_REPLACE, EventBusTags.MAIN_FRAGMENT_DASHBOARD) //携带要切换的目标 Fragment 的索引。
          .navigation();
 ```
 - [HomeFragment](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/fragment/HomeFragment.java)
 - [DashboardFragment](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/fragment/DashboardFragment.java)
 - [NotificationsFragment](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/fragment/NotificationsFragment.java)

#### Activity 获取 ARouter
在 Fragment 通过 ARouter 跳转到 Activity，会触发 Activity 的 **onNewIntent(Intent intent)** 方法回调，所以在 onNewIntent 处理ARouter 携带的要切换的目标 Fragment 的索引，然后通过 BottomNavigationView 的 OnNavigationItemSelectedListener 控制切换 Fragment，同时设置 Toolbar 的 Title。从而完成从一个 Fragment 切换至其他的 Fragment。
```java
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
```
- [MainActivity](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/activity/MainActivity.java)

## 处理配置变化
### 场景描述
当设备旋转或者 Activity 长期处于后台而被系统回收，Activity 的会经历销毁->重建的过程。但是我们可以保存 Fragment，当 Activity 重建时继续使用已经存在的 Fragment 实例，避免浪费系统资源。
### 解决方案
#### setRetainInstance
利用系统 API 提供的 **Fragment#setRetainInstance(boolean retain)** 方法来保存 Fragment 实例，在 **GlobalConfiguration** 的 FragmentLifecycleCallbacks 回调方法里设为 true。
```java
  @Override
  public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
      f.setRetainInstance(true);
  }
```
- [Fragment#setRetainInstance()](https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean))
- [GlobalConfiguration](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/app/GlobalConfiguration.java)

#### findFragmentByTag
因为 FargmentManager 在 Activity 重建时会自动恢复，所以可以在添加 Fragment 时设置 tag，然后通过 **FragmentManager#findFragmentByTag(String tag)** 获取 FragmentManager 中已存在的 Fragment 实例。
这里使用了 **FragmentUtils** 工具类处理 Fragment。
```java
  //处理Activity的重建（recreate），恢复Fragment
  HomeFragment homeFragment;
  DashboardFragment dashboardFragment;
  NotificationsFragment notificationsFragment;
  if (savedInstanceState == null) {
      homeFragment = HomeFragment.newInstance();
      dashboardFragment = DashboardFragment.newInstance();
      notificationsFragment = NotificationsFragment.newInstance();
  } else {
      mReplace = savedInstanceState.getInt(ACTIVITY_FRAGMENT_REPLACE);
      FragmentManager fm = getSupportFragmentManager();
      homeFragment =
              (HomeFragment) FragmentUtils.findFragment(fm, HomeFragment.class);
      dashboardFragment =
              (DashboardFragment) FragmentUtils.findFragment(fm, DashboardFragment.class);
      notificationsFragment =
              (NotificationsFragment) FragmentUtils.findFragment(fm, NotificationsFragment.class);
  }
  if (mFragments == null) {
      mFragments = new ArrayList<>();
      mFragments.add(homeFragment);
      mFragments.add(dashboardFragment);
      mFragments.add(notificationsFragment);
  }
  FragmentUtils.addFragments(getSupportFragmentManager(), mFragments, R.id.main_frame, 0);
```
- [MainActivity](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/activity/MainActivity.java)
- [FragmentUtils](https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/src/main/java/com/blankj/utilcode/util/FragmentUtils.java)

#### 保存当前 Fragment 索引
在 Activity 的 **onSaveInstanceState(Bundle outState)** 保存当前 Fragment 索引：
```java
  @Override
  protected void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      //保存当前Activity显示的Fragment索引
      outState.putInt(ACTIVITY_FRAGMENT_REPLACE, mReplace);
  }
```
在 Activity 的 **initData(Bundle savedInstanceState)** 恢复原来 Fragment 索引：
```java
  mReplace = savedInstanceState.getInt(ACTIVITY_FRAGMENT_REPLACE);
```

## Activity 与 Fragment 通信
在 Activity 中持有 各个 Fragment 实例，MVPArms 的 **IFragment** 接口提供一个 **setData(Object data)** 方法，可以将通信数据传递给目标 Fragment：
```java
  Message message = new Message();
  BaseFragment fragment = (BaseFragment) mFragments.get(mReplace);
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
```
然后在 Fragment 中重写 setData() 方法接收消息，根据消息做一些事情：
```java
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
```

-  [IFragment](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/arms/src/main/java/com/jess/arms/base/delegate/IFragment.java)
- [HomeFragment](https://github.com/xiaobailong24/MVPArms_Fragment/blob/fragment/fragment/src/main/java/me/xiaobailong24/mvparmsfragment/mvp/ui/fragment/HomeFragment.java)

## Fragment 与 Activity 通信
使用 ARouter 将 通信数据携带发送给 Activity，然后在 Activity 的 **onNewIntent(Intent intent)** 接收处理。

<<<<<<< HEAD
## Fragment 之间的通信
Fragment 之间的通信可以通过 Fragment 先与 Activity 通信，然后由 Activity 传递给目标 Fragment。例如上面的通过按钮切换 Fragment 就是一个例子。通过 ARouter 来实现。
=======
## Update
* Tuesday, 13 June 2017: [**ProgressManager**](https://github.com/JessYanCoding/ProgressManager)
* Wednesday, 31 May 2017: [**Template**](https://github.com/JessYanCoding/MVPArmsTemplate)
* Monday, 24 April 2017: [**AppDelegate**](https://github.com/JessYanCoding/MVPArms/wiki#3.12)
* Thursday, 13 April 2017: [**RepositoryManager**](https://github.com/JessYanCoding/MVPArms/wiki#2.3)
* Thursday, 15 December 2016: [**AppManager**](https://github.com/JessYanCoding/MVPArms/wiki#3.11)
* Sunday, 25 December 2016: [**GlobeConfigModule**](https://github.com/JessYanCoding/MVPArms/wiki#3.1)
* Monday, 26 December 2016: [**Version Update**](https://github.com/JessYanCoding/MVPArms/wiki#1.6)
>>>>>>> master


# Github
欢迎 star 和 issue：

- [MVPArms_Fragment](https://github.com/xiaobailong24/MVPArms_Fragment)
- [MVPArms](https://github.com/JessYanCoding/MVPArms)

# 参考文章
1. [我一行代码都不写实现Toolbar!你却还在封装BaseActivity?](http://www.jianshu.com/p/75a5c24174b2)
2. [Activity启动模式图文详解：standard, singleTop, singleTask 以及 singleInstance](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0520/2897.html)
3. [Android中Activity四种启动模式和taskAffinity属性详解](http://blog.csdn.net/zhangjg_blog/article/details/10923643)
