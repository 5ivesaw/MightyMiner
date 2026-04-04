// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.feature.impl.AutoDrillRefuel.states;

import com.jelly.mightyminerv2.feature.impl.AutoDrillRefuel.AutoDrillRefuel;
import com.jelly.mightyminerv2.util.InventoryUtil;
import com.jelly.mightyminerv2.util.KeyBindUtil;
import net.minecraft.client.MinecraftClient;

public class AbiphoneState implements AutoDrillRefuelState{

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onStart(AutoDrillRefuel refueler) {
        int abiphoneSlot = InventoryUtil.getHotbarSlotOfItem("Abiphone");
        if (abiphoneSlot == -1) {
            logError("No abiphone found!");
            refueler.stop();
            refueler.setError(AutoDrillRefuel.AutoDrillRefuelError.NO_ABIPHONE);
            return;
        }

        mc.player.getInventory().selectedSlot = abiphoneSlot;
        KeyBindUtil.rightClick();
        log("Entering abiphone state");
    }

    @Override
    public AutoDrillRefuelState onTick(AutoDrillRefuel refueler) {

        if (InventoryUtil.getInventoryName().contains("Abiphone") && InventoryUtil.isInventoryLoaded()) {
            log("Opened Abiphone GUI");
            int greatforgeSlot = InventoryUtil.getSlotIdOfItemInContainer("Greatforge");

            if(greatforgeSlot == -1){
                logError("No Greatforge contact!");
                refueler.stop();
                refueler.setError(AutoDrillRefuel.AutoDrillRefuelError.NO_GREATFORGE_CONTACT);
                return null;
            }

            InventoryUtil.clickContainerSlot(greatforgeSlot, 0, InventoryUtil.ClickMode.PICKUP);
            return new GreatforgeState();
        }

        return this;
    }

    @Override
    public void onEnd(AutoDrillRefuel refueler) {
        log("Ending abiphone state");
    }
}
