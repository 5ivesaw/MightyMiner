// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.gui;

import com.jelly.mightyminerv2.macro.MacroManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiScreenHandler {

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    public void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType, CallbackInfo ci) {
        if (MacroManager.getInstance().isRunning()) {
            ci.cancel();
        }
    }
}
