package com.jelly.mightyminerv2.util;

import com.jelly.mightyminerv2.config.MightyMinerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public abstract class Logger {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Map<String, String> lastMessages = new HashMap<>();

    public static void addMessage(String text) {
        if (mc.player == null || mc.world == null) {
            System.out.println("MightyMiner " + Formatting.strip(text));
        } else {
            mc.player.sendMessage(Text.literal(text), false);
        }
    }

    public static void sendMessage(String message) { addMessage(formatPrefix("§bMighty Miner", message)); }
    public static void sendWarning(String message)  { addMessage("§c§l[WARNING] §8» §e" + message); }
    public static void sendError(String message)    { addMessage("§l§4§kZ§r§l§4[Mighty Miner]§kH§r §8» §c" + message); }
    public static void sendNote(String message)     { sendMessage(message); }

    public static void sendLog(String message) {
        if (isDuplicate("debug", message)) return;
        if (MightyMinerConfig.debugMode && mc.player != null) {
            addMessage("§l§2[Mighty Miner] §8» §7" + message);
        } else {
            System.out.println("[Mighty Miner] " + message);
        }
    }

    /** Sends a HUD notification (falls back to chat if no notification library available). */
    public static void sendNotification(String title, String message, long duration) {
        if (isDuplicate("notification", message)) return;
        sendMessage("[" + title + "] " + message);
    }

    private static boolean isDuplicate(String type, String message) {
        if (lastMessages.containsKey(type) && lastMessages.get(type).equals(message)) return true;
        lastMessages.put(type, message);
        return false;
    }

    private static String formatPrefix(String prefix, String message) {
        return Formatting.RED + "[" + Formatting.BLUE + prefix + Formatting.RED + "] §8» §e" + message;
    }

    public abstract String getName();
    protected void log(String msg)   { sendLog(formatMessage(msg)); }
    protected void send(String msg)  { sendMessage(formatMessage(msg)); }
    protected void error(String msg) { sendError(formatMessage(msg)); }
    protected void warn(String msg)  { sendWarning(formatMessage(msg)); }
    protected void note(String msg)  { sendNote(formatMessage(msg)); }
    protected void logError(String msg) { sendError(formatMessage(msg)); }
    protected String formatMessage(String msg) { return "[" + getName() + "] " + msg; }
}
