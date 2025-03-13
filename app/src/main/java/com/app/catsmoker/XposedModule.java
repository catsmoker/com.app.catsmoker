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
public class XposedModule implements IXposedHookLoadPackage {

    private static final String TAG = XposedModule.class.getSimpleName();

    // Packages to spoof as OnePlus 12
    private static final String[] packagesToSpoof = {
        "com.activision.callofduty.shooter",
        "com.activision.callofduty.warzone",
        "com.garena.game.codm",
        "com.tencent.tmgp.kr.codm",
        "com.vng.codmvn",
        "com.tencent.tmgp.cod",
        "com.tencent.ig",
        "com.pubg.imobile",
        "com.pubg.krmobile",
        "com.rekoo.pubgm",
        "com.vng.pubgmobile",
        "com.tencent.tmgp.pubgmhd",
        "com.dts.freefiremax",
        "com.dts.freefireth",
        "com.epicgames.fortnite",
        "com.ea.gp.fifamobile",
        "com.gameloft.android.ANMP.GloftA9HM",
        "com.madfingergames.legends",
        "com.pearlabyss.blackdesertm",
        "com.pearlabyss.blackdesertm.gl",
        "com.netease.lztgglobal",
        "com.riotgames.league.wildrift",
        "com.riotgames.league.wildrifttw",
        "com.riotgames.league.wildriftvn",
        "com.riotgames.league.teamfighttactics",
        "com.riotgames.league.teamfighttacticstw",
        "com.riotgames.league.teamfighttacticsvn",
        "com.ngame.allstar.eu",
        "com.mojang.minecraftpe",
        "com.YoStar.AetherGazer",
        "com.miHoYo.GenshinImpact",
        "com.garena.game.lmjx",
        "com.epicgames.portal",
        "com.tencent.lolm",
        "jp.konami.pesam",
        "com.ea.gp.apexlegendsmobilefps",
        "com.mobilelegends.mi",
        "com.levelinfinite.hotta.gp",
        "com.supercell.clashofclans",
        "com.vng.mlbbvn",
        "com.levelinfinite.sgameGlobal",
        "com.tencent.tmgp.sgame",
        "com.mobile.legends",
        "com.proximabeta.mf.uamo",
        "com.tencent.KiHan",
        "com.tencent.tmgp.cf",
        "com.tencent.tmgp.gnyx",
        "com.netease.newspike"
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        String packageName = lpparam.packageName;

        // Check if the package is in the list to spoof
        if (Arrays.asList(packagesToSpoof).contains(packageName)) {
            spoofOnePlus12();
            XposedBridge.log("Spoofed " + packageName + " as OnePlus 12");
        }
    }

    // Method to spoof as OnePlus 12
    private static void spoofOnePlus12() {
        setPropValue("MANUFACTURER", "OnePlus");
        setPropValue("MODEL", "PJD110");
    }

    // Generic method to set Build properties
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
