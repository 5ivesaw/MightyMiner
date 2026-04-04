package com.jelly.mightyminerv2.macro;

import com.jelly.mightyminerv2.config.MightyMinerConfig;
import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.macro.impl.CommissionMacro.CommissionMacro;
import com.jelly.mightyminerv2.macro.impl.GlacialMacro.GlacialMacro;
import com.jelly.mightyminerv2.macro.impl.MiningMacro.MiningMacro;
import com.jelly.mightyminerv2.macro.impl.RouteMiner.RouteMinerMacro;
import com.jelly.mightyminerv2.util.Logger;
import net.minecraft.client.MinecraftClient;

public class MacroManager {
    private static MacroManager instance;
    private AbstractMacro currentMacro;

    public static MacroManager getInstance() {
        if (instance == null) instance = new MacroManager();
        return instance;
    }

    public boolean isRunning() {
        return currentMacro != null && currentMacro.isRunning();
    }

    public void toggle() {
        if (isRunning()) stop();
        else start();
    }

    public void start() {
        currentMacro = getMacroForType(MightyMinerConfig.macroType);
        if (currentMacro != null) {
            Logger.sendMessage("Starting " + currentMacro.getName());
            currentMacro.start();
        }
    }

    public void stop() {
        if (currentMacro != null) {
            currentMacro.stop();
            Logger.sendMessage("Stopped " + currentMacro.getName());
        }
    }

    public AbstractMacro getCurrentMacro() { return currentMacro; }

    private AbstractMacro getMacroForType(int type) {
        return switch (type) {
            case 0 -> CommissionMacro.getInstance();
            case 1 -> GlacialMacro.getInstance();
            case 2 -> MiningMacro.getInstance();
            case 3 -> RouteMinerMacro.getInstance();
            default -> null;
        };
    }
}
