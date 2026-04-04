// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.macro.MacroManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    public ClientPlayerEntity thePlayer;

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isPressed()Z", ordinal = 2))
    public boolean isPressed(KeyBinding instance) {
        return instance.isKeyDown() && !MacroManager.getInstance().isRunning();
    }

    @Redirect(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;isActive()Z", remap = false))
    public boolean isActive() {
        if (MacroManager.getInstance().isRunning()) {
            return true;
        }
        return Display.isActive();
    }
}
