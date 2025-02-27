-keep class com.mb.catsmoker.service.UserService { *; }
-keepclassmembers class com.mb.catsmoker.service.UserService {
    public <init>(...);
}

# Keep annotations
-keep @interface androidx.annotation.** { *; }

# Don't strip native code
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''
-dontshrink

-keep class androidx.transition.** {*;}
-keep class kotlinx.coroutines.android.** {*;}