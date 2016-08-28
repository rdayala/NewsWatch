# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Professional\Dev\Android\sdk/tools/proguard/proguard-android.txt
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

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-printseeds
-dontoptimize
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn com.squareup.okhttp.**
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keep class com.example.model.** { *; }
# Parcel library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-dontwarn okio.**
-dontwarn com.google.gson.**
-dontwarn java.nio.file.**
-dontwarn com.google.android.gms.**
-dontwarn com.bea.xml.**
-dontwarn com.google.vending.**

-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
-keep class org.simpleframework.xml.stream.**{ *; }

-keepattributes ElementList, Root

-keepclassmembers class * {
    @org.simpleframework.xml.* *;
}

# retrofit proguard config
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }
-dontwarn com.squareup.okhttp.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep interface retrofit.** { *;}
-keep interface com.squareup.** { *; }
-dontwarn rx.**
-dontwarn retrofit.**
# realm proguard config
-keep class io.realm.** { *; }
-keep interface io.realm.** { *; }
-keep class com.rdayala.example.newswatch.FavoriteNewsItem
-keep class com.rdayala.example.newswatch.NewsItemTag
-dontwarn javax.**
-dontwarn io.realm.**