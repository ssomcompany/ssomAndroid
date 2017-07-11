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
-dontpreverify

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

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
# The official support library.
-keep class android.support.v4.** { *; }
-keepclassmembers class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keepclassmembers class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

-keep class com.facebook.** { *; }
-keepclassmembers class com.facebook.** { *; }
-dontwarn com.facebook.**

-keepattributes InnerClasses

-keep class com.onesignal.** { *; }
-dontwarn com.onesignal.**

-keepnames class com.google.android.maps.** {*;}
-keep public class com.google.android.maps.** {*;}

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-keep class android.support.** { *; }
-dontwarn android.support.**

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclasseswithmembers enum * {*;}

-keep public class * {
    public protected *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class udk.android.** { *; }
-dontwarn udk.android.**

# okhttp3
-dontnote okhttp3.**
-dontwarn okio.**
-dontwarn okhttp3.**

# retrofit2 proguard warning
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class com.ssomcompany.ssomclient.network.api.** { *; }
-dontwarn com.ssomcompany.ssomclient.network.api.**

-keep class com.ssomcompany.ssomclient.network.model.** { *; }
-dontwarn com.ssomcompany.ssomclient.network.model.**

# fabric for crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# glide for image loading
-keep class com.bumptech.** { *; }
-dontwarn com.bumptech.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}