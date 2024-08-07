package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("DiscouragedPrivateApi")
@SuppressWarnings("ConstantConditions")
public class gameunlocker implements IXposedHookLoadPackage {

    private static final String TAG = gameunlocker.class.getSimpleName();
    // Packages to Spoof as OnePlus 12
    private static final String[] packagesToChangeOP12 = {
            // activision games
            "com.activision.callofduty.shooter",
            "com.garena.game.codm",
            "com.tencent.tmgp.kr.codm",
            "com.vng.codmvn",
            "com.tencent.tmgp.cod",
            // Tencent Games
            "com.tencent.ig",
            "com.pubg.imobile",
            "com.pubg.krmobile",
            "com.rekoo.pubgm",
            "com.vng.pubgmobile",
            "com.tencent.tmgp.pubgmhd",
            // Garena Games
            "com.dts.freefiremax",
            "com.dts.freefireth",
            // Epic Games
            "com.epicgames.fortnite"
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        String packageName = loadPackageParam.packageName;

        // OnePlus
        if (Arrays.asList(packagesToChangeOP12).contains(packageName)) {
            propsToChangeOP12();
            XposedBridge.log("Spoofed " + packageName + " as OnePlus 12");
        }
    }

    // OnePlus
    // Props to Spoof as OnePlus 12
    private static void propsToChangeOP12() {
        setPropValue("MANUFACTURER", "OnePlus");
        setPropValue("MODEL", "PJD110");
    }

    private static void setPropValue(String key, Object value) {
        try {
            Log.d(TAG, "Defining prop " + key + " to " + value.toString());
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            XposedBridge.log("Failed to set prop: " + key + "\n" + Log.getStackTraceString(e));
        }
    }
}