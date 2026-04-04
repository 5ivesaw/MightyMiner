package com.jelly.mightyminerv2.config;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;

/**
 * MightyMiner configuration – Fabric 1.21.10 port.
 * All settings preserved from the original OneConfig version.
 * Saved/loaded as JSON in ./config/mightyminerv2/config.json
 */
@SuppressWarnings("unused")
public class MightyMinerConfig {

    private static final Path CONFIG_FILE = Paths.get("./config/mightyminerv2/config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ─── General ────────────────────────────────────────────────────────────
    public static int    macroType                 = 0;
    public static String miningTool                = "";
    public static String commMiningTool            = "";
    public static String commSlayerWeapon          = "";
    public static int    commMacroMobKillerType    = 0;
    public static String selectedRoute             = "";

    // ─── Pickaxe / Speed ────────────────────────────────────────────────────
    public static int  pickaxeAbilityType    = 0;
    public static int  miningSpeed           = 1500;
    public static int  tickGlideOffset       = 1;
    public static float miningCoefficient   = 1.0f;
    public static float angleCoefficient    = 0.01f;
    public static float distanceCoefficient = 0.005f;
    public static int  cost4                = 3;

    // ─── Commission Macro ────────────────────────────────────────────────────
    public static int    commissionLocation    = 0;
    public static int    commMithrilPriority   = 3;
    public static int    commTitaniumPriority  = 5;
    public static int    commHardStonePriority = 1;
    public static int    commGlacitePriority   = 0;
    public static boolean commAutoGetStats     = true;

    // ─── Glacial Macro ───────────────────────────────────────────────────────
    public static int     glacialMacroType          = 0;
    public static boolean glacialAutoGetStats       = true;

    // ─── Mining Macro ────────────────────────────────────────────────────────
    public static int  miningMacroMithrilPriority    = 3;
    public static int  miningMacroTitaniumPriority   = 5;
    public static int  miningMacroHardStonePriority  = 1;

    // ─── Route Miner ────────────────────────────────────────────────────────
    public static boolean routeMinerLoop = true;

    // ─── Auto Sell ───────────────────────────────────────────────────────────
    public static boolean autoSell               = false;
    public static int     autoSellType           = 0;
    public static int     autoSellBazaarDelay    = 3000;
    public static int     autoSellNpcDelay       = 3000;

    // ─── Failsafe ────────────────────────────────────────────────────────────
    public static boolean bedrockCheckFailsafe  = true;
    public static boolean knockbackFailsafe     = true;
    public static boolean playerFailsafe        = true;
    public static boolean teleportFailsafe      = true;
    public static boolean rotationFailsafe      = true;
    public static boolean itemChangeFailsafe    = true;
    public static boolean badEffectFailsafe     = true;
    public static boolean nameMentionFailsafe   = true;
    public static boolean profileFailsafe       = true;
    public static boolean disconnectFailsafe    = true;
    public static boolean worldChangeFailsafe   = true;
    public static int     failsafeAction        = 0; // 0=stop, 1=logout, 2=reconnect

    // ─── Delays ──────────────────────────────────────────────────────────────
    public static int blockMinerCooldown       = 200;
    public static int rotationSmoothTime       = 150;
    public static int commissionClaimDelay     = 500;
    public static int commissionCheckDelay     = 200;
    public static int screenOpenDelay          = 300;
    public static int inventoryActionDelay     = 100;

    // ─── Misc ─────────────────────────────────────────────────────────────────
    public static boolean precisionMiner   = true;
    public static boolean miscFullBlock    = false;
    public static boolean muteGame        = false;
    public static boolean debugMode       = false;

    // ─── Discord Webhook ─────────────────────────────────────────────────────
    public static String discordWebhookUrl = "";
    public static boolean discordEnabled   = false;

    // ─── Scheduler ───────────────────────────────────────────────────────────
    public static boolean schedulerEnabled = false;
    public static int     schedulerRunTime  = 60;
    public static int     schedulerStopTime = 10;

    // ────────────────────────────────────────────────────────────────────────
    public void load() {
        try {
            if (!Files.exists(CONFIG_FILE)) { save(); return; }
            JsonObject obj = JsonParser.parseReader(new FileReader(CONFIG_FILE.toFile())).getAsJsonObject();
            for (Field field : MightyMinerConfig.class.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                if (obj.has(field.getName())) {
                    field.setAccessible(true);
                    JsonElement el = obj.get(field.getName());
                    if (field.getType() == int.class)     field.setInt(null, el.getAsInt());
                    else if (field.getType() == float.class) field.setFloat(null, el.getAsFloat());
                    else if (field.getType() == boolean.class) field.setBoolean(null, el.getAsBoolean());
                    else if (field.getType() == String.class)  field.set(null, el.getAsString());
                }
            }
        } catch (Exception e) { System.err.println("[MightyMiner] Failed to load config: " + e.getMessage()); }
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            JsonObject obj = new JsonObject();
            for (Field field : MightyMinerConfig.class.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Object val = field.get(null);
                if (val instanceof Integer)  obj.addProperty(field.getName(), (Integer) val);
                else if (val instanceof Float)   obj.addProperty(field.getName(), (Float) val);
                else if (val instanceof Boolean) obj.addProperty(field.getName(), (Boolean) val);
                else if (val instanceof String)  obj.addProperty(field.getName(), (String) val);
            }
            try (Writer w = new FileWriter(CONFIG_FILE.toFile())) {
                GSON.toJson(obj, w);
            }
        } catch (Exception e) { System.err.println("[MightyMiner] Failed to save config: " + e.getMessage()); }
    }
}
