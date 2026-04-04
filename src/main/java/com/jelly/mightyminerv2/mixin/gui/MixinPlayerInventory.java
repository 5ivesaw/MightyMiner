package com.jelly.mightyminerv2.mixin.gui;

import com.jelly.mightyminerv2.macro.MacroManager;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {
    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void blockScrollWhenRunning(double scrollAmount, CallbackInfo ci) {
        if (MacroManager.getInstance().isRunning()) ci.cancel();
    }
}
