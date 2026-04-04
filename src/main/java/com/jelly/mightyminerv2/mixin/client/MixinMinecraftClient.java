package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.macro.MacroManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow public net.minecraft.client.network.ClientPlayerEntity player;

    /** Prevent the game from dropping items when the macro is running. */
    @Redirect(method = "tick",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z",
                       ordinal = 2))
    private boolean preventDrop(KeyBinding kb) {
        return kb.wasPressed() && !MacroManager.getInstance().isRunning();
    }

    /** Keep window focus when the macro is running (so it doesn't pause). */
    @Redirect(method = "onWindowFocusChanged",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/MinecraftClient;isWindowFocused()Z"))
    private boolean keepFocus(MinecraftClient self) {
        return MacroManager.getInstance().isRunning() || self.isWindowFocused();
    }
}
