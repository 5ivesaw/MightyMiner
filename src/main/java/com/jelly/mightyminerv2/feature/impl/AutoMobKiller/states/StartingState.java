// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.feature.impl.AutoMobKiller.states;

import com.jelly.mightyminerv2.feature.impl.AutoMobKiller.AutoMobKiller;
import net.minecraft.entity.LivingEntity;

public class StartingState implements AutoMobKillerState {

    @Override
    public void onStart(AutoMobKiller mobKiller) {
        log("Entering Starting State");
    }

    @Override
    public AutoMobKillerState onTick(AutoMobKiller mobKiller) {
        return new FindMobState();
    }

    @Override
    public void onEnd(AutoMobKiller mobKiller) {
        log("Exiting Starting State");
    }

}
