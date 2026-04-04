package com.jelly.mightyminerv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

import java.util.*;

public class InventoryUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean holdItem(String item) {
        int slot = getHotbarSlotOfItem(item);
        if (slot == -1) return false;
        mc.player.getInventory().selectedSlot = slot;
        return true;
    }

    public static int getSlotIdOfItemInContainer(String item) {
        return getSlotIdOfItemInContainer(item, false);
    }

    public static int getSlotIdOfItemInContainer(String item, boolean equals) {
        Slot s = getSlotOfItemInContainer(item, equals);
        return s != null ? s.id : -1;
    }

    public static Slot getSlotOfItemInContainer(String item) {
        return getSlotOfItemInContainer(item, false);
    }

    public static Slot getSlotOfItemInContainer(String item, boolean equals) {
        if (mc.player.currentScreenHandler == null) return null;
        for (Slot s : mc.player.currentScreenHandler.slots) {
            if (!s.hasStack()) continue;
            String name = strip(s.getStack().getName().getString());
            if (equals ? name.equalsIgnoreCase(item) : name.contains(item)) return s;
        }
        return null;
    }

    public static int getHotbarSlotOfItem(String item) {
        if (item.isEmpty()) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.hasCustomName()) continue;
            if (stack.getName().getString().contains(item)) return i;
        }
        return -1;
    }

    public static List<String> getMissingItemsInInventory(Collection<String> required) {
        List<String> missing = new ArrayList<>(required);
        for (ItemStack s : mc.player.getInventory().main) {
            if (!s.isEmpty()) missing.removeIf(item -> s.getName().getString().contains(item));
        }
        return missing;
    }

    public static boolean areItemsInInventory(Collection<String> items) {
        return getMissingItemsInInventory(items).isEmpty();
    }

    public static List<String> getMissingItemsInHotbar(Collection<String> required) {
        List<String> missing = new ArrayList<>(required);
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isEmpty()) missing.removeIf(item -> s.getName().getString().contains(item));
        }
        return missing;
    }

    public static boolean areItemsInHotbar(Collection<String> items) {
        return getMissingItemsInHotbar(items).isEmpty();
    }

    public static String getInventoryName() {
        if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler h) {
            return h.getInventory().getName().getString();
        }
        return "";
    }

    public static void clickContainerSlot(int slot, int mouseButton, int clickMode) {
        if (mc.player.currentScreenHandler == null) return;
        mc.interactionManager.clickSlot(
            mc.player.currentScreenHandler.syncId, slot, mouseButton,
            SlotActionType.values()[clickMode], mc.player);
    }

    public static void swapSlots(int slot, int hotbarSlot) {
        mc.interactionManager.clickSlot(
            mc.player.playerScreenHandler.syncId, slot, hotbarSlot,
            SlotActionType.SWAP, mc.player);
    }

    public static void closeScreen() {
        if (mc.currentScreen != null && mc.player != null) mc.player.closeHandledScreen();
    }

    public static List<String> getItemLore(ItemStack stack) {
        List<String> lore = new ArrayList<>();
        if (stack.isEmpty() || stack.getNbt() == null) return lore;
        NbtList tag = stack.getNbt().getCompound("display").getList("Lore", NbtElement.STRING_TYPE);
        for (int i = 0; i < tag.size(); i++) lore.add(strip(tag.getString(i)));
        return lore;
    }

    public static List<String> getItemLoreFromOpenContainer(String name) {
        if (mc.player.currentScreenHandler == null) return new ArrayList<>();
        for (Slot s : mc.player.currentScreenHandler.slots) {
            if (!s.hasStack()) continue;
            if (!strip(s.getStack().getName().getString()).contains(name)) continue;
            return getItemLore(s.getStack());
        }
        return new ArrayList<>();
    }

    public static List<String> getItemLoreFromInventory(String name) {
        for (Slot s : mc.player.playerScreenHandler.slots) {
            if (!s.hasStack()) continue;
            if (!strip(s.getStack().getName().getString()).contains(name)) continue;
            return getItemLore(s.getStack());
        }
        return new ArrayList<>();
    }

    public static boolean isInventoryLoaded() {
        if (mc.player == null || mc.currentScreen == null) return false;
        if (!(mc.currentScreen instanceof GenericContainerScreen)) return false;
        GenericContainerScreenHandler h = (GenericContainerScreenHandler) mc.player.currentScreenHandler;
        ItemStack last = h.getInventory().getStack(h.size() - 1);
        return !last.isEmpty();
    }

    public static boolean isInventoryEmpty() {
        for (int i = 0; i < mc.player.size(); i++) {
            if (!mc.player.getInventory().getStack(i).isEmpty()) return false;
        }
        return true;
    }

    public static String getItemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        try {
            return stack.getNbt().getCompound("ExtraAttributes").getString("id");
        } catch (Exception e) {
            return strip(stack.getName().getString());
        }
    }

    public static int getDrillFuelCapacity(String drillName) {
        for (String lore : getItemLoreFromInventory(drillName)) {
            if (!lore.startsWith("Fuel: ")) continue;
            try { return Integer.parseInt(lore.split("/")[1].replace("k","000")); } catch (Exception ignored) {}
        }
        return -1;
    }

    public static int getDrillRemainingFuel(String drillName) {
        for (String lore : getItemLoreFromInventory(drillName)) {
            if (!lore.startsWith("Fuel: ")) continue;
            try { return Integer.parseInt(lore.split(" ")[1].split("/")[0].replace(",","")); } catch (Exception ignored) {}
        }
        return -1;
    }

    private static String strip(String s) {
        String r = Formatting.strip(s);
        return r != null ? r : s;
    }

    public enum ClickType  { LEFT, RIGHT }
    public enum ClickMode  { PICKUP, QUICK_MOVE, SWAP }
}
