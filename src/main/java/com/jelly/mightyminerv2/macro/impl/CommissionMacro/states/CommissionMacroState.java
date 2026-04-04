// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.macro.impl.CommissionMacro.states;

import com.jelly.mightyminerv2.macro.impl.CommissionMacro.CommissionMacro;
import com.jelly.mightyminerv2.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public interface CommissionMacroState {

    void onStart(CommissionMacro macro);

    CommissionMacroState onTick(CommissionMacro macro);

    void onEnd(CommissionMacro macro);

    default void log(String message) {
        System.out.println("[" + this.getClass().getSimpleName() + "] " + message);
    }

    default void logError(String message) {
        System.out.println("[" + this.getClass().getSimpleName() + "] ERROR: " + message);
    }

    default void send(String message) {
        Logger.addMessage("[" + this.getClass().getSimpleName() + "] " + message);
    }

}
