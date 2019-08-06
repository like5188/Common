# 关键字：
# dontwarn                      通常用于处理我们无法解决的第三方 library 的警告
# keep                          保留类和类中的成员，防止被混淆或移除
# keepnames                     保留类和类中的成员，防止被混淆，成员没有被引用会被移除
# keepclassmembers              只保留类中的成员，防止被混淆或移除
# keepclassmembernames          只保留类中的成员，防止被混淆，成员没有引用会被移除
# keepclasseswithmembers        保留类和类中的成员，防止被混淆或移除，保留指明的成员
# keepclasseswithmembernames    保留类和类中的成员，防止被混淆，保留指明的成员，成员没有引用会被移除

# 通配符：
# <field>	匹配类中的所有字段
# <method>	匹配类中所有的方法
# <init>	匹配类中所有的构造函数
# *	        匹配任意长度字符，不包含包名分隔符(.)
# **	    匹配任意长度字符，包含包名分隔符(.)
# ***	    匹配任意参数类型
# $	        匹配内部类

# 哪些不该混淆：
# 使用了自定义控件那么要保证它们不参与混淆
# 使用了枚举要保证枚举不被混淆
# 对第三方库中的类不进行混淆
# 运用了反射的类也不进行混淆
# 使用了 Gson 之类的工具要使 JavaBean 类即实体类不被混淆
# 在引用第三方库的时候，一般会标明库的混淆规则的，建议在使用的时候就把混淆规则添加上去，免得到最后才去找
# 有用到 WebView 的 JS 调用也需要保证写的接口方法不混淆，原因和第一条一样
# Parcelable 的子类和 Creator 静态成员变量不混淆，否则会产生 Android.os.BadParcelableException 异常
# 使用的四大组件，自定义的Application* 实体类
# JNI中调用的类
# Layout布局使用的View构造函数（自定义控件）、android:onClick等。

#ToolbarUtils
-keep class com.like.common.view.toolbar.CustomActionProvider{*;}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
 **[] $VALUES;
 public *;
}

#matisse 知乎图片选择器
-dontwarn com.squareup.picasso.**