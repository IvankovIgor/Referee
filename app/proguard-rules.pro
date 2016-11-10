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

#-keep class * extends com.technopark.ivankov.referee
#-keepattributes InnerClasses

-keep class com.google.gson.** { *; }
-keep public class com.technopark.ivankov.referee.content.Action { *; }
-keep public class com.technopark.ivankov.referee.content.MatchList$MatchConfig { *; }
-keep public class com.technopark.ivankov.referee.content.MatchList$Match { *; }
-keep public class com.technopark.ivankov.referee.content.TeamList$Team { *; }
-keep public class com.technopark.ivankov.referee.content.PlayerList$Player { *; }
-keep public enum com.technopark.ivankov.referee.content.Action$EventType { *; }
-keep public enum com.technopark.ivankov.referee.content.MatchList$MatchStatus { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}