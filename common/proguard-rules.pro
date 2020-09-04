# 关键字：
# dontwarn                      通常用于处理我们无法解决的第三方库的警告
# keep                          保留类和类成员，防止被移除或者被重命名
# keepnames                     保留类和类成员，防止被重命名
# keepclassmembers              只保留类成员，防止被移除或者被重命名
# keepclassmembernames          只保留类成员，防止被重命名
# keepclasseswithmembers        如果拥有某成员，保留类和类成员，防止被移除或者被重命名
# keepclasseswithmembernames    如果拥有某成员，保留类和类成员，防止被重命名

# 通配符：
# <field>	匹配类中的所有字段
# <method>	匹配类中所有的方法
# <init>	匹配类中所有的构造函数
# *	        匹配任意长度字符，不包含包名分隔符(.)
# **	    匹配任意长度字符，包含包名分隔符(.)
# ***	    匹配任意参数类型
# …         匹配任意长度的任意类型参数。比如void test(…)就能匹配任意 void test(String a) 或者是 void test(int a, String b) 这些方法。
# $	        匹配内部类

# 哪些不该混淆：
# jni方法不可混淆，因为这个方法需要和native方法保持一致；
#-keepclasseswithmembernames class * { # 保持native方法不被混淆
#    native <methods>;
#}
# 使用enum类型时需要注意避免以下两个方法混淆，因为enum类的特殊性，以下两个方法会被反射调用。
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
# 使用第三方开源库或者引用其他第三方的SDK包时，如果有特别要求，也需要在混淆文件中加入对应的混淆规则；
# 运用了反射的类也不进行混淆
# 使用了 Gson、fastjson 之类的工具要使 JavaBean 类即实体类不被混淆
# 有用到 WebView 的 JS 调用也需要保证写的接口方法不混淆
# Parcelable 的子类和 Creator 静态成员变量不混淆，否则会产生 Android.os.BadParcelableException 异常
#-keep class * implements Android.os.Parcelable { # 保持Parcelable不被混淆
#    public static final Android.os.Parcelable$Creator *;
#}

# 如果组件化时，在 module 中使用了自定义的 Application，它又实现了 IModuleApplication 接口，为了反射获取其实例，就需要保护这个实现。
-keep class com.like.common.base.IModuleApplication
-keep class * implements com.like.common.base.IModuleApplication

# 如果使用@AutoWired注解。
-keepclasseswithmembers class * {
    @com.like.common.util.AutoWired <fields>;
}