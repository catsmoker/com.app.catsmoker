package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressLint("DiscouragedPrivateApi")
@SuppressWarnings("ConstantConditions")
public class GameUnlocker implements IXposedHookLoadPackage {

    private static final String TAG = GameUnlocker.class.getSimpleName();

    // Map of packages to spoof with OnePlus 12 properties
    private static final Map<String, Map<String, String>> packagesToSpoof = new HashMap<String, Map<String, String>>() {{
        put("com.YoStar.AetherGazer", createOP12Props());
        put("com.activision.callofduty.shooter", createOP12Props());
        put("com.activision.callofduty.warzone", createOP12Props());
        put("com.dts.freefiremax", createOP12Props());
        put("com.dts.freefireth", createOP12Props());
        put("com.ea.gp.apexlegendsmobilefps", createOP12Props());
        put("com.ea.gp.fifamobile", createOP12Props());
        put("com.epicgames.fortnite", createOP12Props());
        put("com.epicgames.portal", createOP12Props());
        put("com.gameloft.android.ANMP.GloftA9HM", createOP12Props());
        put("com.garena.game.codm", createOP12Props());
        put("com.garena.game.kgvn", createOP12Props());
        put("com.garena.game.lmjx", createOP12Props());
        put("com.levelinfinite.hotta.gp", createOP12Props());
        put("com.levelinfinite.sgameGlobal", createOP12Props());
        put("com.madfingergames.legends", createOP12Props());
        put("com.miHoYo.GenshinImpact", createOP12Props());
        put("com.mobile.legends", createOP12Props());
        put("com.mobilelegends.mi", createOP12Props());
        put("com.mojang.minecraftpe", createOP12Props());
        put("com.netease.lztgglobal", createOP12Props());
        put("com.ngame.allstar.eu", createOP12Props());
        put("com.pearlabyss.blackdesertm.gl", createOP12Props());
        put("com.pearlabyss.blackdesertm", createOP12Props());
        put("com.proximabeta.mf.uamo", createOP12Props());
        put("com.pubg.imobile", createOP12Props());
        put("com.pubg.krmobile", createOP12Props());
        put("com.rekoo.pubgm", createOP12Props());
        put("com.riotgames.league.teamfighttactics", createOP12Props());
        put("com.riotgames.league.teamfighttacticstw", createOP12Props());
        put("com.riotgames.league.teamfighttacticsvn", createOP12Props());
        put("com.riotgames.league.wildrift", createOP12Props());
        put("com.riotgames.league.wildrifttw", createOP12Props());
        put("com.riotgames.league.wildriftvn", createOP12Props());
        put("com.supercell.clashofclans", createOP12Props());
        put("com.tencent.KiHan", createOP12Props());
        put("com.tencent.ig", createOP12Props());
        put("com.tencent.lolm", createOP12Props());
        put("com.tencent.tmgp.cf", createOP12Props());
        put("com.tencent.tmgp.cod", createOP12Props());
        put("com.tencent.tmgp.gnyx", createOP12Props());
        put("com.tencent.tmgp.kr.codm", createOP12Props());
        put("com.tencent.tmgp.pubgmhd", createOP12Props());
        put("com.tencent.tmgp.sgame", createOP12Props());
        put("com.vng.codmvn", createOP12Props());
        put("com.vng.mlbbvn", createOP12Props());
        put("com.vng.pubgmobile", createOP12Props());
        put("jp.konami.pesam", createOP12Props());
        put("vng.games.revelation.mobile", createOP12Props());
    }};

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String packageName = loadPackageParam.packageName;

        if (packagesToSpoof.containsKey(packageName)) {
            Map<String, String> propsToChange = packagesToSpoof.get(packageName);
            if (propsToChange != null) {
                spoofProperties(propsToChange);
                XposedBridge.log("Spoofed " + packageName + " as OnePlus 12");
            }
        }
    }

    private static void spoofProperties(Map<String, String> properties) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            setPropValue(entry.getKey(), entry.getValue());
        }
    }

    private static void setPropValue(String key, String value) {
        try {
            Log.d(TAG, "Setting property " + key + " to " + value);
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            String errorMessage = "Failed to set property: " + key + " to " + value;
            Log.e(TAG, errorMessage, e);
            XposedBridge.log(errorMessage + "\n" + Log.getStackTraceString(e));
        }
    }

    private static Map<String, String> createOP12Props() {
        Map<String, String> props = new HashMap<>();
        props.put("MANUFACTURER", "OnePlus");
        props.put("MODEL", "PJD110");
        return props;
    }
}