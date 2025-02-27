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
