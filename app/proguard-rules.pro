# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple 
# optimizations will remove the generic signature such as class 
# merging and argument removal. See: 
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.SoundScapeApp.soundscape.SoundScapeApp.data.Playlist { *; }
-keep class com.SoundScapeApp.soundscape.SoundScapeApp.data.videoPlaylist { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class kotlin.coroutines.Continuation

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations