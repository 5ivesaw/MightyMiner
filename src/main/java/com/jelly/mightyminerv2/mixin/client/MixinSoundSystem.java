package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.config.MightyMinerConfig;
import com.jelly.mightyminerv2.failsafe.FailsafeManager;
import com.jelly.mightyminerv2.macro.MacroManager;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {
    @Inject(method = "getVolume", at = @At("RETURN"), cancellable = true)
    private void muteGame(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        if (MacroManager.getInstance().isRunning()
                && MightyMinerConfig.muteGame
                && FailsafeManager.getInstance().emergencyQueue.isEmpty()
                && !FailsafeManager.getInstance().triggeredFailsafe.isPresent()) {
            cir.setReturnValue(0f);
        }
    }
}
