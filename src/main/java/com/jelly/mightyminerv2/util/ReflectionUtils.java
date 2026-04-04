package com.jelly.mightyminerv2.util;

public class ReflectionUtils {
    public static boolean hasPackageInstalled(String packageName) {
        try {
            Class.forName(packageName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
