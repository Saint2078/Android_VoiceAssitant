package com.tenke.lib_common;

/**
 * <p>
 * This is a mark interface for classes which implement this won't be obfuscated by Proguard.
 * <p>
 * Common cases are:
 * 1. Gson or xml mapping objects
 * 2. Jni
 * 3. Reflection used
 * 4. AndroidManifest declared
 * 5. WebView JS invoked
 * 6. Parcelable subclasses
 * you may add case if you recall something
 */
public interface NonProguard {

}
