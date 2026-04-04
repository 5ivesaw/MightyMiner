package com.jelly.mightyminerv2.util;

import com.google.common.collect.ImmutableMap;
import com.jelly.mightyminerv2.mixin.client.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class KeyBindUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final KeyBinding[] allKeys = {
        mc.options.attackKey, mc.options.useKey,
        mc.options.backKey, mc.options.forwardKey, mc.options.leftKey, mc.options.rightKey,
        mc.options.jumpKey, mc.options.sneakKey, mc.options.sprintKey
    };
    public static final KeyBinding[] allKeys2 = {
        mc.options.backKey, mc.options.forwardKey, mc.options.leftKey,
        mc.options.rightKey, mc.options.jumpKey
    };
    public static final Map<KeyBinding, Integer> keyBindMap = ImmutableMap.of(
        mc.options.forwardKey,  0,
        mc.options.leftKey,    90,
        mc.options.backKey,   180,
        mc.options.rightKey,  -90
    );

    public static void rightClick()  { ((MinecraftClientAccessor) mc).invokeDoItemUse(); }
    public static void leftClick()   { ((MinecraftClientAccessor) mc).invokeDoAttack(); }
    public static void middleClick() { ((MinecraftClientAccessor) mc).invokeDoItemPick(); }

    public static int  getRightClickDelayTimer()  { return ((MinecraftClientAccessor) mc).getItemUseCooldown(); }
    public static void resetRightClickDelayTimer(){ ((MinecraftClientAccessor) mc).setItemUseCooldown(0); }

    public static void setKeyBindState(KeyBinding key, boolean pressed) {
        if (key == null) return;
        if (pressed && mc.currentScreen != null) { realSetKeyBindState(key, false); return; }
        realSetKeyBindState(key, pressed);
    }

    private static void realSetKeyBindState(KeyBinding key, boolean pressed) {
        if (key == null) return;
        int code = key.getDefaultKey().getCode();
        KeyBinding.setKeyPressed(InputUtil.fromKeyCode(code, 0), pressed);
        if (pressed && !key.isPressed()) key.setPressed(true);
        else if (!pressed && key.isPressed()) key.setPressed(false);
    }

    public static void stopMovement() { stopMovement(false); }
    public static void stopMovement(boolean ignoreAttack) {
        realSetKeyBindState(mc.options.forwardKey, false);
        realSetKeyBindState(mc.options.backKey,    false);
        realSetKeyBindState(mc.options.rightKey,   false);
        realSetKeyBindState(mc.options.leftKey,    false);
        if (!ignoreAttack) {
            realSetKeyBindState(mc.options.attackKey, false);
            realSetKeyBindState(mc.options.useKey,    false);
        }
        realSetKeyBindState(mc.options.sneakKey,  false);
        realSetKeyBindState(mc.options.jumpKey,   false);
        realSetKeyBindState(mc.options.sprintKey, false);
    }

    public static void holdThese(boolean withAttack, KeyBinding... keys) {
        releaseAllExcept(keys);
        for (KeyBinding k : keys) if (k != null) realSetKeyBindState(k, true);
        if (withAttack) realSetKeyBindState(mc.options.attackKey, true);
    }
    public static void holdThese(KeyBinding... keys) {
        releaseAllExcept(keys);
        for (KeyBinding k : keys) if (k != null) realSetKeyBindState(k, true);
    }

    public static void releaseAllExcept(KeyBinding... keys) {
        for (KeyBinding k : allKeys)
            if (k != null && !contains(keys, k) && k.isPressed()) realSetKeyBindState(k, false);
    }

    public static boolean contains(KeyBinding[] arr, KeyBinding key) {
        for (KeyBinding k : arr)
            if (k != null && k.getDefaultKey().getCode() == key.getDefaultKey().getCode()) return true;
        return false;
    }

    public static boolean areAllKeybindsReleased() {
        for (KeyBinding k : allKeys2) if (k != null && k.isPressed()) return false;
        return true;
    }

    public static List<KeyBinding> getNeededKeyPresses(Vec3d orig, Vec3d dest) {
        double[] delta = {orig.x - dest.x, orig.z - dest.z};
        float req = (float)(Math.toDegrees(Math.atan2(delta[0], -delta[1])));
        float diff = AngleUtil.normalizeAngle(req - mc.player.getYaw()) * -1;
        List<KeyBinding> keys = new ArrayList<>();
        keyBindMap.forEach((k, yaw) -> {
            if (Math.abs(yaw - diff) < 67.5 || Math.abs(yaw - (diff + 360.0)) < 67.5) keys.add(k);
        });
        return keys;
    }

    public static List<KeyBinding> getNeededKeyPresses(float neededYaw) {
        neededYaw = AngleUtil.normalizeAngle(neededYaw - mc.player.getYaw()) * -1;
        float ny = neededYaw;
        List<KeyBinding> keys = new ArrayList<>();
        keyBindMap.forEach((k, yaw) -> {
            if (Math.abs(yaw - ny) < 67.5 || Math.abs(yaw - (ny + 360.0)) < 67.5) keys.add(k);
        });
        return keys;
    }
}
