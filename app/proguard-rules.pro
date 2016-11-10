# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/nyashcore/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keep public enum com.technopark.ivankov.referee.content.Action$EventType { *; }
#-keep public enum com.technopark.ivankov.referee.content.Action$** {
#    **[] $VALUES;
#    public *;
#}
-keep public enum com.technopark.ivankov.referee.content.MatchList$MatchStatus { *; }
-keep class * extends com.technopark.ivankov.referee
-keep public class com.technopark.ivankov.referee.content.MatchList$MatchConfig { *; }
-keep public class com.technopark.ivankov.referee.content.MatchList$Match { *; }
-keep public class com.technopark.ivankov.referee.content.TeamList$Team { *; }
-keep public class com.technopark.ivankov.referee.content.PlayerList$Player { *; }
#-keep public enum com.technopark.ivankov.referee.content.MatchList$** {
#    **[] $VALUES;
#    public *;
#}
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-dontpreverify
#-repackageclasses ''
#-allowaccessmodification
#-optimizations !code/simplification/arithmetic
#-keepattributes *Annotation*
#
#-keep public class *
#
#-keepclassmembers class * { *; }

#-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-dontpreverify
#-ignorewarnings
#-verbose
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#
#-keepattributes *Annotation*
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
#
#
##-libraryjars libs/asmack-android-19-0.8.10.jar
##-libraryjars libs/android-support-v4.jar
#
## 保持 native 方法不被混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
## 保持自定义控件类不被混淆
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
#
#-keepclasseswithmembers class * {
#    void onClick*(...);
#}
#-keepclasseswithmembers class * {
#    *** *Callback(...);
#}
#
## keep setters in Views so that animations can still work.
## see http://proguard.sourceforge.net/manual/examples.html#beans
#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}
#
## 保持自定义控件类不被混淆
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
## 保持枚举 enum 类不被混淆
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 保持 Parcelable 不被混淆
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
#-keep class * implements android.os.Serializable {
#  public static final android.os.Serializable$Creator *;
#}
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
## http client
#-keep class org.apache.http.** {*; }
#-keep class org.apache.**{*;}
#
## umeng message anysl
## 以下类过滤不混淆
#-keep public class * extends com.umeng.**
## 以下包不进行过滤
#-keep class com.umeng.** { *; }
#-keepclassmembers class * {
#   public <init>(org.json.JSONObject);
#}
#
#-keep,allowshrinking class org.android.agoo.service.* {
#    public <fields>;
#    public <methods>;
#}
#-keep,allowshrinking class com.umeng.message.* {
#    public <fields>;
#    public <methods>;
#}
#
#-keep public class com.airi.buyue.R$*{
#    public static final int *;
#}
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep public class com.umeng.fb.ui.ThreadView {
#}
##是 universalimageloader
#-dontwarn com.nostra13.universalimageloader.**
#
#-keep class com.nostra13.universalimageloader.** {
#    *;
#}
#
## v4 包的混淆
#
##-libraryjars ./libs/android-support-v4.jar
#
##-dontwarn android.support.**
#
#-dontwarn android.support.v4.**
#
#-dontwarn **CompatHoneycomb
#
#-dontwarn **CompatHoneycombMR2
#
#-dontwarn **CompatCreatorHoneycombMR2
#
#-keep interface android.support.v4.app.** { *; }
#
#-keep class android.support.v4.** { *; }
#
#-keep public class * extends android.support.v4.**
#
#-keep public class * extends android.app.Fragment
##alipay
#-keep class com.alipay.android.app.**{*;}
#
#-keepattributes Signature
#
#-keepattributes *Annotation*
#
##-libraryjars src/main/ormlite-android-4.48.jar
##-libraryjars src/main/ormlite-core-4.48.jar
#
#-dontwarn com.j256.**
#
#-keep class com.j256.** { *; }
#-keep class com.j256.ormlite.** { *; }
#
#-keep public class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
#-keep public class * extends com.j256.ormlite.android.apptools.OpenHelperManager
#-keep public class * extends com.j256.ormlite.dao
#-dontwarn com.j256.ormlite.android.**
#-dontwarn com.j256.ormlite.dao.**
#-dontwarn com.j256.ormlite.db.**
#-dontwarn com.j256.ormlite.field.**
#-dontwarn com.j256.ormlite.logger.**
#-dontwarn com.j256.ormlite.misc.**
#-dontwarn com.j256.ormlite.stmt.**
#-dontwarn com.j256.ormlite.support.**
#-dontwarn com.j256.ormlite.table.**
#-dontwarn com.j256.ormlite.**
#-dontwarn com.j256.ormlite.android.**
#-dontwarn com.j256.ormlite.field.**
#-dontwarn com.j256.ormlite.stmt.**
#
#-keep class com.j256.ormlite.** { *; }
#-keep class com.j256.ormlite.android.** { *; }
#-keep class com.j256.ormlite.field.** { *; }
#-keep class com.j256.ormlite.dao.** { *; }
#-keep class com.j256.ormlite.db.** { *; }
#-keep class com.j256.ormlite.stmt.** { *; }
#
##无法保存 bean
#-keep class com.ishow.funnymap.bean.** { *; }
##保护<init>(Context context) 不被混淆
#-keepclasseswithmembers class * {
#    public <init>(android.content.Context);
#}
#
##个推
#-dontwarn com.igexin.**
#-keep class com.igexin.**{*;}
#
##ACRA specifics
## we need line numbers in our stack traces otherwise they are pretty useless
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#
## ACRA needs "annotations" so add this...
##-keepattributes *Annotation*
#
## volley
#-dontwarn com.android.volley.jar.**
#-keep class com.android.volley.**{*;}
#
#
#-keepattributes *Annotation*
#-keep public class * extends android.app.Activity
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.app.backup.BackupAgent
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends android.app.Fragment
#-keep public class com.android.vending.licensing.ILicensingService
#-keep class com.itheima.mobilesafe.engine.AppInfoProvider
#-keep class net.youmi.android.** {
#*;
#}
#
#-keep class com.airi.buyue.data.**{*;}
#-keep class com.airi.buyue.table.**{*;}
#-dontshrink
#-dontoptimize
#-dontwarn com.google.android.maps.**
#-dontwarn android.webkit.WebView
#-dontwarn com.umeng.**
#-dontwarn com.tencent.weibo.sdk.**
#-dontwarn com.facebook.**
##-libraryjars src/main/SocialSDK_QQZone_2.jar
#-keep enum com.facebook.**
#-keepattributes Exceptions,InnerClasses,Signature
#-keepattributes *Annotation*
#-keepattributes SourceFile,LineNumberTable
#-keep public interface com.facebook.**
#-keep public interface com.tencent.**
#-keep public interface com.umeng.socialize.**
#-keep public interface com.umeng.socialize.sensor.**
#-keep public interface com.umeng.scrshot.**
#-keep public class com.umeng.socialize.* {*;}
#-keep public class javax.**
#-keep public class android.webkit.**
#-keep class com.facebook.**
#-keep class com.umeng.scrshot.**
#-keep public class com.tencent.** {*;}
#-keep class com.umeng.socialize.sensor.**
#-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
#-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
#-keep class im.yixin.sdk.api.YXMessage {*;}
#-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
#-keep public class com.airi.buyue.R$*{
#    public static final int *;
#}
#
#-keep class com.baidu.mapapi.** {*;}
#
##-libraryjars src/main/libs/locSDK_5.0.jar
#-keep class com.baidu.** { *; }
#-keep class vi.com.gdi.bgl.android.**{*;}
#
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
#-dontshrink
#-dontoptimize
#-dontwarn com.google.android.maps.**
#-dontwarn android.webkit.WebView
#-dontwarn com.umeng.**
#-dontwarn com.tencent.weibo.sdk.**
#-dontwarn com.facebook.**
#
##-libraryjars src/main/libs/SocialSDK_QQZone_2.jar
#
#-keep enum com.facebook.**
#-keepattributes Exceptions,InnerClasses,Signature
#-keepattributes *Annotation*
#-keepattributes SourceFile,LineNumberTable
#
#-keep public interface com.facebook.**
#-keep public interface com.tencent.**
#-keep public interface com.umeng.socialize.**
#-keep public interface com.umeng.socialize.sensor.**
#-keep public interface com.umeng.scrshot.**
#
#-keep public class com.umeng.socialize.* {*;}
#-keep public class javax.**
#-keep public class android.webkit.**
#
#-keep class com.facebook.**
#-keep class com.umeng.scrshot.**
#-keep public class com.tencent.** {*;}
#-keep class com.umeng.socialize.sensor.**
#
#-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
#
#-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
#
#-keep class im.yixin.sdk.api.YXMessage {*;}
#-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
#
#-keep public class com.airi.buyue.R$*{
#    public static final int *;
#}
#
#-keep,allowshrinking class org.android.agoo.service.* {
#    public <fields>;
#    public <methods>;
#}
#
#-keep,allowshrinking class com.umeng.message.* {
#    public <fields>;
#    public <methods>;
#}
#
#-keep public class com.airi.buyue.R$*{
#   public static final int *;
#}
#
#-keep class com.airi.buyue.Card{*;}
#-keep class com.airi.buyue.UserCard{*;}
#-keep class com.airi.buyue.GuideCard{*;}
#-keep class com.airi.buyue.User{*;}
#-keep class com.airi.buyue.table.Event{*;}
#-keep class com.airi.buyue.table.Sync{*;}
#-keep class com.airi.buyue.table.Board{*;}
#-keep class com.airi.buyue.table.Ntf{*;}
#
##-libraryjars src/main/libs/com.umeng.message.lib.jar
##-libraryjars src/main/libs/httpmime-4.1.3.jar
##-libraryjars src/main/libs/locSDK_5.0.jar
##-libraryjars src/main/libs/ormlite-android-4.48.jar
##-libraryjars src/main/libs/ormlite-core-4.48.jar
##-libraryjars src/main/libs/SocialSDK_QQZone_1.jar
##-libraryjars src/main/libs/SocialSDK_QQZone_2.jar
##-libraryjars src/main/libs/SocialSDK_QQZone_3.jar
##-libraryjars src/main/libs/SocialSDK_Sina.jar
##-libraryjars src/main/libs/SocialSDK_UserCenter.jar
##-libraryjars src/main/libs/SocialSDK_WeiXin_1.jar
##-libraryjars src/main/libs/SocialSDK_WeiXin_2.jar
##-libraryjars src/main/libs/umeng-update-v2.4.2.jar
##-libraryjars src/main/libs/umeng_social_sdk.jar
#
##-libraryjars /libs/android-support-v4.jar  -dontwarn android.support.v4.**
##amap
#-keep class android.support.v4.** {*;}
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment
#-keep class com.amap.api.**  {*;}
#-keep class com.autonavi.**  {*;}
#-keep class com.a.a.**  {*;}
#
#-keep class com.amp.apis.lib.**{*;}
#-keep class com.squareup.picasso.**{*;}
#
###-libraryjars   libs/android-support-v4.jar
##-dontwarn android.support.v4.**
##-keep class android.support.v4.** { *; }
##-keep interface android.support.v4.app.** { *; }
##-keep public class * extends android.support.v4.**
##-keep public class * extends android.app.Fragment
##
###-libraryjars   libs/MapApiLocation.jar
###-libraryjars   libs/MapApiSearch.jar
###-libraryjars   libs/Android_Map_V2.0.4.jar
##
##-dontwarn com.amap.api.**
##-dontwarn com.a.a.**
##-dontwarn com.autonavi.**
##
##-keep class com.amap.api.**  {*;}
##-keep class com.autonavi.**  {*;}
##-keep class com.a.a.**  {*;}
#
#
#-dontwarn android.support.v4.**
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep public class * extends android.support.v4.**
#-keep public class * extends android.app.Fragment
#-dontwarn com.amap.api.**
#-dontwarn com.a.a.**
#-dontwarn com.autonavi.**
#-keep class com.amap.api.**  {*;}
#-keep class com.autonavi.**  {*;}
#-keep class com.a.a.**  {*;}
#
#-keep class com.amap.api.mapcore.**{*;}
#-keep class com.amap.api.maps.**{*;}
#-keep class com.autonavi.amap.mapcore.*{*;}
#-keep class com.amap.api.location.**{*;}
#-keep class com.aps.**{*;}
#-keep class com.amap.api.services.**{*;}
#
##//umeng mssg
#-keep class com.umeng.message.* {
#    public <fields>;
#    public <methods>;
#}
#
#-keep class com.umeng.message.protobuffer.MessageResponse$PushResponse$Info {
#    public <fields>;
#    public <methods>;
#}
#
#-keep class com.umeng.message.protobuffer.MessageResponse$PushResponse$Info$Builder {
#    public <fields>;
#    public <methods>;
#}
#
#-keep class org.android.agoo.impl.*{
#    public <fields>;
#    public <methods>;
#}
#
#-keep class org.android.agoo.service.* {*;}
#
#-keep class org.android.spdy.**{*;}
#
#-keep public class com.airi.buyue.R$*{
#    public static final int *;
#}
#-keep class org.springframework.**
#
#-dontwarn butterknife.internal.**
#-keep class **$$ViewInjector { *; }
#-keepnames class * { @butterknife.InjectView *;}