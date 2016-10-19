# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/cellz/Downloads/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
# 대소문자 구분
-dontusemixedcaseclassnames
# public 으로 설정되지 않은 class
-dontskipnonpubliclibraryclasses
# 독자적 수행
-dontoptimize
-dontpreverify

-keepattributes EnclosingMethod
-dontwarn android.support.**

-verbose

# androidManifest 가 참조하는 것
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.app.preference.Preference
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# 보조 라이브러리 클래스 보존
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends android.app.Fragment

# view 의 애니메이터
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

#FOR APPCOMPAT 23.1.1:
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.* { *; }

-keep class com.facebook.** { *; }
-dontwarn com.facebook.**

-keepattributes InnerClasses

-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class com.onesignal.** { *; }
-dontwarn com.onesignal.**
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

-keep class android.location.** { *; }

-keepnames class com.google.android.maps.** {*;}
-keep public class com.google.android.maps.** {*;}