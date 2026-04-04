// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.failsafe.impl;

import com.jelly.mightyminerv2.failsafe.AbstractFailsafe;
import com.jelly.mightyminerv2.macro.MacroManager;
import com.jelly.mightyminerv2.util.Logger;
import lombok.Getter;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Formatting;

public class ProfileFailsafe extends AbstractFailsafe {

    @Getter
    private static final ProfileFailsafe instance = new ProfileFailsafe();

    private static final String TRIGGER_PHRASE = "Profile";

    @Override
    public String getName() {
        return "ProfileFailsafe";
    }

    @Override
    public Failsafe getFailsafeType() {
        return Failsafe.PLAYER_PROFILE_OPEN;
    }

    @Override
    public int getPriority() { return 2; }

    @Override
    public boolean onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) {
            return false;
        }

        if (event.gui instanceof GenericContainerScreen) {
            GenericContainerScreen guiChest = (GenericContainerScreen) event.gui;

            if (guiChest.slots instanceof GenericContainerScreenHandler) {
                GenericContainerScreenHandler container = (GenericContainerScreenHandler) guiChest.slots;
                IInventory chestInventory = container.getLowerChestInventory();

                if (!chestInventory.isEmpty() && chestInventory.hasCustomName()) {
                    String inventoryName = Formatting.strip(chestInventory.getName().getString());
                    if (inventoryName.toLowerCase().contains(TRIGGER_PHRASE.toLowerCase())) {
                        note("Detected inventory open with name containing " + inventoryName);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean react() {
        if (mc.currentScreen != null && mc.player != null) {
            mc.addScheduledTask(() -> {
                if (mc.currentScreen != null && mc.player != null) {
                    mc.player.closeScreen();
                    note("Closing the menu... continuing");
                }
            });
        } else {
            warn("Menu already closed... continuing");
        }
        return true;
    }
}