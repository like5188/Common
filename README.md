#### 最新版本

模块|Common
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/Common.svg)](https://jitpack.io/#like5188/Common)

## 功能介绍
1、base包中提供了 BaseActivity、BaseFragment、BaseDialogFragment、BaseApplication、BaseComponentApplication（用于组件化开发） 五个基类。

2、提供了很多常用工具类、自定义View。

3、常用第三方库的引用及其混淆的添加。

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

2、常用第三方库的引用
```java
    api "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    api 'com.google.android.material:material:1.2.1'
    api 'androidx.constraintlayout:constraintlayout:2.0.1'
    api 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
    api 'com.google.android:flexbox:1.0.0'
    api 'androidx.activity:activity-ktx:1.2.0-alpha08'
    api 'androidx.fragment:fragment-ktx:1.3.0-alpha08'
    // 调色板
    api 'androidx.palette:palette:1.0.0'

    // koin依赖注入库
    api "org.koin:koin-androidx-viewmodel:2.1.6"

    // coil图片加载库
    api 'io.coil-kt:coil:0.12.0'
    api 'io.coil-kt:coil-gif:0.12.0'
    api 'io.coil-kt:coil-svg:0.12.0'
    api 'io.coil-kt:coil-video:0.12.0'

    // room
    api 'androidx.room:room-runtime:2.2.5'
    api 'androidx.room:room-ktx:2.2.5'// room 对协程的支持：suspend
//    kapt 'androidx.room:room-compiler:2.2.5'

    // work
    api 'androidx.work:work-runtime-ktx:2.4.0'// workmanager 对协程的支持：suspend

    // zxing。（zxing 3.4.0：要求最低api等级为24）
    api 'com.google.zxing:core:3.3.3'

    // 扫描器
    api 'com.shouzhong:Scanner:1.1.2-beta1'
    api 'com.shouzhong:ScannerZBarLib:1.0.0'// zbar

    // PhotoView
    api 'com.github.chrisbanes:PhotoView:2.3.0'

    // 图片选择器
    api 'com.github.LuckSiege.PictureSelector:picture_library:v2.5.8'

    // 日期时间格式化工具
    api 'net.danlew:android.joda:2.10.1.2'

    api 'com.github.OCNYang:PageTransformerHelp:v1.0.1'

    // 汉字转拼音
    api 'com.github.promeg:tinypinyin:2.0.3'

    // 选择器
    api 'com.contrarywind:Android-PickerView:4.1.9'

    // 手写签名
    api 'com.github.gcacace:signature-pad:1.2.1'

    // VideoView视频边播放边缓存
    api 'com.danikula:videocache:2.7.1'

    // pdf浏览器
    api 'es.voghdev.pdfviewpager:library:1.1.0'

    // websocket
    api 'org.java-websocket:Java-WebSocket:1.4.1'

    // coroutines
    api 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8'
    api 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.8'
    // livedata 对协程的支持：liveData{}
    api 'androidx.lifecycle:lifecycle-livedata-ktx:2.2.0'
    // Activity 或 Fragment 对协程的支持：lifecycleScope
    api 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0'
    // ViewModel 对协程的支持：viewModelScope
    api 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0'
//    // Annotation processor
//    kapt 'androidx.lifecycle:lifecycle-compiler:2.2.0'
//    // alternately - if using Java8, use the following instead of lifecycle-compiler
//    api 'androidx.lifecycle:lifecycle-common-java8:2.2.0'
```