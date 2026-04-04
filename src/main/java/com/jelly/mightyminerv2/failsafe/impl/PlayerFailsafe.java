// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.failsafe.impl;

import com.jelly.mightyminerv2.failsafe.AbstractFailsafe;
import com.jelly.mightyminerv2.macro.MacroManager;
import com.jelly.mightyminerv2.util.EntityUtil;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: Check if it causes excessive lag before re-implementing!
public class PlayerFailsafe extends AbstractFailsafe {

    @Getter
    private static final PlayerFailsafe instance = new PlayerFailsafe();
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Map<Entity, Long> playerStaringTimes = new HashMap<>();

    @Override
    public String getName() {
        return "PlayerFailsafe";
    }

    @Override
    public Failsafe getFailsafeType() {
        return Failsafe.TELEPORT;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public boolean onTick(ClientTickEvent event) {
        if (mc.world == null || mc.player == null) return false;

        List<Entity> players = mc.world.loadedEntityList.stream().filter(
                (entity) -> entity instanceof AbstractClientPlayer && entity != mc.player && !EntityUtil.isNpc(entity)
        ).collect(Collectors.toList());

        boolean playerBlocking = false;
        long currentTime = System.currentTimeMillis();
        playerStaringTimes.keySet().removeIf(player -> !players.contains(player));

        for (Entity player : players) {
            double distanceSquared = player.getPosition().distanceSq(mc.player.getPosition());
            boolean isBlocking = distanceSquared < 3;
            boolean isLookingAtUs = isPlayerLookingAtMe(player);

            if (isLookingAtUs && isBlocking) {
                if (!playerStaringTimes.containsKey(player)) {
                    playerStaringTimes.put(player, currentTime);
                    return false;
                }

                long staringDuration = currentTime - playerStaringTimes.get(player);

                if (staringDuration > 1000) {
                    playerBlocking = true;
                    break;
                }
            } else {
                playerStaringTimes.remove(player);
            }
        }

        return playerBlocking;
    }

    private boolean isPlayerLookingAtMe(Entity player) {
        Vec3d lookVec = player.getLook(1.0F);
        Vec3d playerToMeVec = new Vec3d(
                mc.player.getX() - player.getX(),
                mc.player.getBoundingBox().minY + mc.player.getEyeHeight() - (player.getY() + player.getEyeHeight()),
                mc.player.getZ() - player.getZ()
        );

        double d0 = playerToMeVec.lengthVector();
        playerToMeVec = playerToMeVec.normalize();

        double dot = lookVec.dotProduct(playerToMeVec);
        double fovCosine = Math.cos(Math.toRadians(30F));
        return dot > fovCosine;
    }

    @Override
    public boolean onChat(/* ClientChatReceivedEvent - use FabricEventBridge */ event) {
        return event.message.getString().toLowerCase().contains(mc.getSession().getUsername().toLowerCase());
    }

    @Override
    public boolean react() {
        warn("Stopping macro due to player nearby.");
        MacroManager.getInstance().disable();
        return true;
    }

}
