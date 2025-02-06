# Keep SQLCipher classes
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Prevent stripping of native methods
-keepclassmembers class net.sqlcipher.database.SQLiteDatabase {
    native <methods>;
}
