#### 最新版本

模块|LibCommon
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/LibCommon.svg)](https://jitpack.io/#like5188/LibCommon)

## 功能介绍
1、BaseApplication，集成了ARouter，并且提供了BaseAppComponent实例，用于获取BaseApplication实例。

2、IModuleApplication，组件的application类必须实现的接口。注意：组件的application类并不是真正的Application子类。只是功能类似而已。实际上此类只是实现IModuleApplication接口，并没有继承Application。

3、BaseActivity、BaseFragment，集成了ARouter。

4、集成了常用库

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
        compile 'com.github.like5188:LibCommon:版本号'
    }
```

2、引用的库
```java
    // constraint
    api 'com.android.support.constraint:constraint-layout:1.1.3'
    // support
    api 'com.android.support:appcompat-v7:28.0.0'
    api 'com.android.support:design:28.0.0'
    api 'com.android.support:recyclerview-v7:28.0.0'
    // anko 包含了协程相关的库
    api('org.jetbrains.anko:anko:0.10.8') {
        exclude group: 'com.android.support'
    }
    // kotlin
    api 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.10'
    api "org.jetbrains.kotlin:kotlin-reflect:1.3.10"
    // rxjava2
    api 'io.reactivex.rxjava2:rxjava:2.2.2'
    api 'io.reactivex.rxjava2:rxkotlin:2.3.0'
    api 'io.reactivex.rxjava2:rxandroid:2.1.0'
    // fastjson
    api 'com.alibaba:fastjson:1.2.51'
    // 65535
    api 'com.android.support:multidex:1.0.3'
    // retrofit2
    api 'com.squareup.retrofit2:retrofit:2.4.0'
    api 'com.squareup.retrofit2:converter-gson:2.4.0'
    api 'com.squareup.okhttp3:logging-interceptor:3.11.0'
    // dagger
    api 'com.google.dagger:dagger:2.17'
    api 'com.google.dagger:dagger-android:2.17'
    api 'com.google.dagger:dagger-android-support:2.17'
    // arouter
    api('com.alibaba:arouter-api:1.4.0') {
        exclude group: 'com.android.support'
    }
    // room
    api 'android.arch.persistence.room:runtime:1.1.1'
    api 'android.arch.persistence.room:rxjava2:1.1.1'
    // lifecycle
    api 'android.arch.lifecycle:extensions:1.1.1'
    // paging
    api('android.arch.paging:runtime:1.0.1') {
        exclude group: 'com.android.support'
    }
    api "android.arch.paging:rxjava2:1.0.1"
    // work
    api('android.arch.work:work-runtime-ktx:1.0.0-alpha11') {
        exclude group: 'android.arch.lifecycle'
    }
    // easypermissions
    api 'pub.devrel:easypermissions:1.1.3'
    // glide
    api("com.github.bumptech.glide:glide:4.8.0") {
        exclude group: "com.android.support"
    }
    api "com.github.bumptech.glide:okhttp3-integration:4.8.0"
    api 'jp.wasabeef:glide-transformations:4.0.1'// glide对应的图片处理库，可以转换图片为圆形、圆角矩形、高斯模糊等等效果
    // zxing
    api 'com.google.zxing:core:3.3.3'
    // rxbinding
    api 'com.jakewharton.rxbinding2:rxbinding:2.2.0'
    // PhotoView
    api 'com.github.chrisbanes:PhotoView:2.1.4'// 不能升级2.2.0，因为使用了AndroidX库，不能和support库共存。
    // 图片选择器
    api 'com.github.LuckSiege.PictureSelector:picture_library:v2.2.3'

    api 'com.github.like5188:LibRetrofit:1.0.2'
    api 'com.github.like5188:Repository:1.2.1'
    api 'com.github.like5188.LiveDataBus:livedatabus:1.2.1'
    api 'com.github.like5188:LiveDataRecyclerView:1.2.1'

    kapt 'com.github.bumptech.glide:compiler:4.8.0'
    kapt 'com.github.like5188.LiveDataBus:livedatabus_compiler:1.2.1'
    kapt 'com.google.dagger:dagger-compiler:2.17'
    kapt 'com.google.dagger:dagger-android-processor:2.17'
```

3、Proguard
```java
    #ToolbarUtils
    -keep class com.like.lib_common.view.toolbar.CustomActionProvider{*;}

    #anko
    -dontwarn org.jetbrains.anko.**

    #Arouter
    -keep public class com.alibaba.android.arouter.routes.**{*;}
    -keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
    # 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
    -keep interface * implements com.alibaba.android.arouter.facade.template.IProvider{*;}
    # 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
    -keep class * implements com.alibaba.android.arouter.facade.template.IProvider{*;}
    # 如果在非 Activity 的类中使用了 @Autowired 注解注入，需添加下面规则，以防注入失败
    -keepnames class * {
      @com.alibaba.android.arouter.facade.annotation.Autowired <fields>;
    }
    -dontwarn com.alibaba.android.arouter.facade.model.RouteMeta

    -keep class com.like.lib_router.router.** { *; }
    -keep class com.like.lib_router.service.** { *; }

    # FastJson 混淆代码
    -dontwarn com.alibaba.fastjson.**
    -keep class com.alibaba.fastjson.** { *; }

    # 如果在module中使用了Application，实现了 IModuleApplication 接口，就需要保护实现，用于反射。
    -keep class * implements com.like.lib_common.application.IModuleApplication{*;}

    # Retrofit
    # Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
    # EnclosingMethod is required to use InnerClasses.
    -keepattributes Signature, InnerClasses, EnclosingMethod
    # Retain service method parameters when optimizing.
    -keepclassmembers,allowshrinking,allowobfuscation interface * {
      @retrofit2.http.* <methods>;
    }
    # Ignore annotation used for build tooling.
    -dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
    # Ignore JSR 305 annotations for embedding nullability information.
    -dontwarn javax.annotation.**
    # Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
    -dontwarn kotlin.Unit
    # Top-level functions that can only be used by Kotlin.
    -dontwarn retrofit2.-KotlinExtensions
    -dontwarn org.conscrypt.**

    -keep class com.like.retrofit.** { *; }
    -dontwarn com.like.retrofit.**

    #livedatabus
    -keep class * extends com.like.livedatabus.Bridge
    -keep class com.like.livedatabus_annotations.**{*;}

    #repository
    -keep class com.like.repository.** { *; }

    #glide
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.module.AppGlideModule
    -keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
    }

    #matisse 知乎图片选择器
    -dontwarn com.squareup.picasso.**

    #rxjava
    -dontwarn sun.misc.**
    -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
    }

    #rxandroid
    -dontwarn sun.misc.**
    -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
     long producerIndex;
     long consumerIndex;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
      rx.internal.util.atomic.LinkedQueueNode producerNode;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
      rx.internal.util.atomic.LinkedQueueNode consumerNode;
    }
```
