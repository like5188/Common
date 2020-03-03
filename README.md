#### 最新版本

模块|Common
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/Common.svg)](https://jitpack.io/#like5188/Common)

## 功能介绍
1、base包中提供了 BaseActivity、BaseFragment、BaseDialogFragment、BaseApplication、BaseComponentApplication 五个基类。

2、BaseComponentApplication 用于组件化时主程序使用。其中的 IModuleApplication，是组件的application类必须实现的接口。
注意：组件的 application 类并没有真正继承 Application 类。只是功能类似而已，都用于一些功能的初始化。
实际上此类只是实现 IModuleApplication 接口，然后 BaseComponentApplication 类通过代理的方式来管理这些类的生命周期。

3、IModuleApplication 接口用于组件化时组件使用，是组件的 application 类必须实现的接口。
注意：①组件实现了此接口后，还必须要有一个 public 的无参构造函数，用于反射构造组件 Application 的实例。
②必须在组件的 AndroidManifest.xml 文件中进行如下配置：<meta-data android:name="实现类的全限定类名" android:value="IModuleApplication" />

4、提供了很多常用工具类、自定义View。

5、常用第三方库的引用。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        compile 'com.github.like5188:Common:版本号'
    }
```

2、基类
```java
    BaseActivity
    BaseApplication
    BaseComponentApplication.kt
    BaseDialogFragment
    BaseFragment
```

3、常用工具类
```java
    validator
    ApkUtils
    AppUtils
    ArrayUtils
    BcdUtils
    ByteUtils
    CheckManager
    ClickTextViewSpanUtils
    ClickUtils
    CloseableEx.kt
    DateUtils
    DimensionUtils
    DoubleFormatUtils
    Executors.kt
    FilterUtils
    GlideEngineForPictureSelector
    GlideUtils.kt
    HexUtil
    HighLightUtils
    ImageUtils
    InputSoftKeybordUtils
    JsonEx.kt
    LifecycleEx.kt
    ListEx.kt
    Logger
    MD5Utils
    NetWorkEx.kt
    NotificationEx.kt
    PermissionUtils
    PhoneUtils
    RadioManager
    RectEx.kt
    RxJavaUtils
    SelectorDrawableEx.kt
    SerializableUtils
    SingletonHolder
    SPUtils.kt
    StatusBarUtils
    StorageUtils
    TabLayoutUtils
    TextViewEx.kt
    TimerUtils
    ToastEx.kt
    UnitEx.kt
    UriEx.kt
    VibrateUtils
    ViewEx.kt
    ZXingUtils
```

4、自定义View
```java
    badgeview
    callback
    dragview
    pwdedittext
    titlebar
    AspectRatioImageView
    CircleTextView
    ContainsEmojiEditText
    ExpandView
    MyGridView
    MyVideoView
    RotateTextView
    SidebarView
    SquareImageView
    TimerTextView
```

5、常用第三方库的引用
```java
    api "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    api 'com.google.android.material:material:1.1.0'
    api 'androidx.recyclerview:recyclerview:1.1.0'
    api 'androidx.constraintlayout:constraintlayout:1.1.3'
    // 调色板
    api 'androidx.palette:palette:1.0.0'

    // rxjava2
    api 'io.reactivex.rxjava2:rxjava:2.2.11'
    api 'io.reactivex.rxjava2:rxkotlin:2.3.0'
    api 'io.reactivex.rxjava2:rxandroid:2.1.1'
    api 'com.github.tbruyelle:rxpermissions:0.10.2'

    // fastjson
    api 'com.alibaba:fastjson:1.2.51'

    // retrofit2
    api 'com.squareup.retrofit2:retrofit:2.6.2'
    api 'com.squareup.retrofit2:converter-gson:2.6.2'
    api 'com.squareup.retrofit2:adapter-rxjava2:2.6.2'
    api 'com.squareup.retrofit2:converter-scalars:2.6.2'
    api 'com.squareup.okhttp3:logging-interceptor:3.12.2'

    // dagger
    api 'com.google.dagger:dagger:2.17'
    api 'com.google.dagger:dagger-android:2.17'
    api 'com.google.dagger:dagger-android-support:2.17'

    // arouter
    api 'com.alibaba:arouter-api:1.5.0'

    // ViewModelProviders
    api 'androidx.lifecycle:lifecycle-extensions:2.2.0'

    // paging
    api 'androidx.paging:paging-runtime:2.1.1'

    // work
    api 'androidx.work:work-runtime-ktx:2.3.2'// workmanager 对协程的支持：suspend

    // room
    api 'androidx.room:room-runtime:2.2.4'
    api 'androidx.room:room-rxjava2:2.2.4'
    api 'androidx.room:room-ktx:2.2.4'// room 对协程的支持：suspend

    // coroutines
    api 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2'
    api 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2'
    api 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0'// Activity 或 Fragment 对协程的支持：lifecycleScope
    api 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'// ViewModel 对协程的支持：viewModelScope
    api 'androidx.lifecycle:lifecycle-livedata-ktx:2.2.0'// livedata 对协程的支持：liveData{}

    // glide
    api "com.github.bumptech.glide:glide:4.8.0"
    api "com.github.bumptech.glide:okhttp3-integration:4.8.0"
    api 'jp.wasabeef:glide-transformations:4.0.1'// glide对应的图片处理库，可以转换图片为圆形、圆角矩形、高斯模糊等等效果

    // zxing。（zxing 3.4.0：要求最低api等级为24）
    api 'com.google.zxing:core:3.3.3'

    // 扫描器
    api 'com.shouzhong:Scanner:1.1.2-beta1'

    // rxbinding
    api 'com.jakewharton.rxbinding2:rxbinding:2.2.0'

    // PhotoView
    api 'com.github.chrisbanes:PhotoView:2.3.0'

    // 图片选择器
    api 'com.github.LuckSiege.PictureSelector:picture_library:v2.4.6'

    // 日期时间格式化工具
    api 'net.danlew:android.joda:2.10.1.2'

    api 'com.google.android:flexbox:1.0.0'

    api 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0-alpha03'

    api 'com.github.OCNYang:PageTransformerHelp:v1.0.1'

    // 汉字转拼音
    api 'com.github.promeg:tinypinyin:2.0.3'

    // 选择器
    api 'com.contrarywind:Android-PickerView:4.1.9'

    // 手写签名
    api 'com.github.gcacace:signature-pad:1.2.1'

    // 使用 Glide 时需要
    kapt 'com.github.bumptech.glide:compiler:4.8.0'
    //    kapt 'androidx.lifecycle:lifecycle-compiler:2.1.0'
    //    kapt 'androidx.room:room-compiler:2.2.1'
    //    kapt 'com.google.dagger:dagger-compiler:2.17'
    //    kapt 'com.google.dagger:dagger-android-processor:2.17'
```