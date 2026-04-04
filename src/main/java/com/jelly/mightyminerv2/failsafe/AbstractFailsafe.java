// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.failsafe;

import com.jelly.mightyminerv2.event.BlockChangeEvent;
import com.jelly.mightyminerv2.event.PacketEvent;
import com.jelly.mightyminerv2.util.Logger;
import net.minecraft.client.MinecraftClient;

import net.minecraftforge.client.event.GuiOpenEvent;


import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public abstract class AbstractFailsafe {

    protected final Minecraft mc = MinecraftClient.getInstance();

    public abstract int getPriority();

    public abstract String getName();

    public abstract Failsafe getFailsafeType();

    public boolean onBlockChange(BlockChangeEvent event) {
        return false;
    }

    public boolean onPacketReceive(PacketEvent.Received event) {
        return false;
    }

    public boolean onTick(net.minecraft.client.MinecraftClient event) {
        return false;
    }

    public boolean onChat(/* ClientChatReceivedEvent - use FabricEventBridge */ event) {
        return false;
    }

    public boolean onGuiOpen(GuiOpenEvent event) { return false; }

    public boolean onWorldUnload(/* WorldUnload */ event) {
        return false;
    }

    public boolean onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        return false;
    }

    public abstract boolean react();

    public void resetStates() {}

    protected void log(String message) {
        Logger.sendLog(formatMessage(message));
    }

    protected void send(String message) {
        Logger.sendMessage(formatMessage(message));
    }

    protected void error(String message) {
        Logger.sendError(formatMessage(message));
    }

    protected void warn(String message) {
        Logger.sendWarning(formatMessage(message));
    }

    protected void note(String message) {
        Logger.sendNote(formatMessage(message));
    }

    protected String formatMessage(String message) {
        return "[" + getName() + "] " + message;
    }

    public enum Failsafe {
        BAD_EFFECTS,
        BLOCK_CHANGE,
        DISCONNECT,
        ITEM_CHANGE,
        KNOCKBACK,
        ROTATION,
        TELEPORT,
        BEDROCK_CHECK,
        SLOT_CHANGE,
        PLAYER_PROFILE_OPEN,
        NAME_MENTION
    }
}