// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.failsafe.impl;

import com.google.common.collect.ImmutableSet;
import com.jelly.mightyminerv2.event.PacketEvent;
import com.jelly.mightyminerv2.failsafe.AbstractFailsafe;
import com.jelly.mightyminerv2.macro.MacroManager;
import lombok.Getter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;


import java.util.Set;

public class BadEffectFailsafe extends AbstractFailsafe {

    @Getter
    private static final BadEffectFailsafe instance = new BadEffectFailsafe();
    private final Set<Integer> BAD_EFFECTS = ImmutableSet.of(
            Potion.poison.id,
            Potion.wither.id,
            Potion.weakness.id,
            Potion.blindness.id,
            Potion.hunger.id,
            Potion.moveSlowdown.id,
            Potion.digSlowdown.id
    );

    @Override
    public String getName() {
        return "BadEffectFailsafe";
    }

    @Override
    public Failsafe getFailsafeType() {
        return Failsafe.BAD_EFFECTS;
    }

    @Override
    public int getPriority() {
        return 7;
    }

    @Override
    public boolean onTick(net.minecraft.client.MinecraftClient event) {
        for (PotionEffect effect : mc.player.getActivePotionEffects()) {
            if (BAD_EFFECTS.contains(effect.getPotionID())) return true;
        }

        return false;
    }

    @Override
    public boolean react() {
        MacroManager.getInstance().disable();
        warn("Bad effect detected! Disabling macro.");
        return true;
    }

}
