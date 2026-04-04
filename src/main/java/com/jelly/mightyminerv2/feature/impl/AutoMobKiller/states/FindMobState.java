// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.feature.impl.AutoMobKiller.states;

import com.jelly.mightyminerv2.config.MightyMinerConfig;
import com.jelly.mightyminerv2.feature.impl.AutoMobKiller.AutoMobKiller;
import com.jelly.mightyminerv2.feature.impl.Pathfinder;
import com.jelly.mightyminerv2.util.BlockUtil;
import com.jelly.mightyminerv2.util.EntityUtil;
import com.jelly.mightyminerv2.util.PlayerUtil;
import com.jelly.mightyminerv2.util.helper.Clock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FindMobState implements AutoMobKillerState {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Clock waitForMobsToSpawnTimer = new Clock();

    @Override
    public void onStart(AutoMobKiller mobKiller) {
        log("Entering Find Mob State");
        waitForMobsToSpawnTimer.reset();
    }

    @Override
    public AutoMobKillerState onTick(AutoMobKiller mobKiller) {

        LivingEntity mob = findBestMob(mobKiller);

        if (mob == null) {
            if (!waitForMobsToSpawnTimer.isScheduled()) {
                log("Scheduled a 10-second timer to see if mobs spawn or not");
                waitForMobsToSpawnTimer.schedule(10_000);
            }

            if (waitForMobsToSpawnTimer.isScheduled() && waitForMobsToSpawnTimer.passed()) {
                log("Cannot find a mob to kill!");
                mobKiller.setError(AutoMobKiller.MKError.NO_ENTITIES);
                mobKiller.stop();
                return null;
            }

            return this;
        }

        mobKiller.setTargetMob(mob);
        mobKiller.setTargetMobOriginalPos(mob.getPos());
        return new PathfindingState();
    }

    private LivingEntity findBestMob(AutoMobKiller mobKiller) {
        Minecraft mc = MinecraftClient.getInstance();
        List<EntityPlayer> playerEntities = new ArrayList<>(MinecraftClient.getInstance().theWorld.playerEntities);
        playerEntities.remove(mc.player);

        List<EntityPlayer> actualPlayers = playerEntities.stream()
                .filter(player -> !EntityUtil.isNpc(player))
                .collect(Collectors.toList());

        List<LivingEntity> mobs = EntityUtil.getEntities(mobKiller.getMobsToKill(), mobKiller.getBlacklistedMobs());

        if(mobs.isEmpty())
            return null;

        List<LivingEntity> filteredMobs = mobs.stream()
                .filter(mob -> mc.player.getDistanceSqToEntity(mob) < 1000)
                .filter(mob -> actualPlayers.stream()
                        .noneMatch(player -> player.getDistanceSqToEntity(mob) < 9.0f))
                .filter(mob -> BlockUtil.canStandOn(EntityUtil.getBlockStandingOn(mob)))
                .collect(Collectors.toList());

        return filteredMobs.stream()
                .min(Comparator.comparingDouble(mob -> mc.player.getDistanceSqToEntity(mob)))
                .orElse(null);
    }


    @Override
    public void onEnd(AutoMobKiller mobKiller) {
        log("Exiting Find Mob State");
    }

}
