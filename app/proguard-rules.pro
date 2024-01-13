# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class javax.** { *; }
-keep class org.** { *; }
-keep class twitter4j.** { *; }
-keep class org.apache.commons.codec.binary.**
-keep interface org.apache.commons.codec.binary.**
-keep enum org.apache.commons.codec.binary.**

-keep class org.slf4j.**
-keep class org.apache.commons.** { *; }
-keep interface org.slf4j.**
-keep enum org.slf4j.**
-keep class com.sun.syndication.io.**
-keep interface com.sun.syndication.io.**
-keep enum com.sun.syndication.io.**
-keep class com.sun.syndication.feed.synd.**
-keep interface com.sun.syndication.feed.synd.**
-keep enum com.sun.syndication.feed.synd.**

-keep class com.google.gson.examples.android.model.** { *; }

-libraryjars <java.home>/lib/rt.jar(java/**,javax/**)


-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.roughike.bottombar.**
-dontwarn com.squareup.okhttp.**

-keep class com.aditya.filebrowser.FileBrowser**
-keep class com.aditya.filebrowser.FileChooser**

-dontwarn android.support.v7.**
-keep class android.support.v7.widget.** { *; }
-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keep class org.apache.** { *; }
-keepclassmembers class org.apache.** { *; }
-keep class oauth.signpost.** { *; }


#-ignorewarnings


-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}






#-obfuscationdictionary /home/mwss/Scrivania/Project_buildings/Android_project/Lome/Lome_FREE/dict.txt
#-packageobfuscationdictionary /home/mwss/Scrivania/Project_buildings/Android_project/Lome/Lome_FREE/dict.txt
#-classobfuscationdictionary /home/mwss/Scrivania/Project_buildings/Android_project/Lome/Lome_FREE/dict.txt

