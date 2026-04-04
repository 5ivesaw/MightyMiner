// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("timer")
    Timer getTimer();

    @Accessor("leftClickCounter")
    void setLeftClickCounter(int leftClickCounter);

    @Accessor("rightClickDelayTimer")
    int getRightClickDelayTimer();

    @Accessor("rightClickDelayTimer")
    void setRightClickDelayTimer(int rightClickDelayTimer);

    @Invoker("clickMouse")
    void leftClick();

    @Invoker("rightClickMouse")
    void rightClick();

    @Invoker("middleClickMouse")
    void middleClick();
}
