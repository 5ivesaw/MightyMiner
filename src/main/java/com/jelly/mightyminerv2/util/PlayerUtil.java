package com.jelly.mightyminerv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.function.Predicate;

public class PlayerUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static BlockPos getBlockStandingOn() {
        return new BlockPos(
            MathHelper.floor(mc.player.getX()),
            (int)(Math.ceil(mc.player.getY() - 0.25) - 1),
            MathHelper.floor(mc.player.getZ())
        );
    }

    public static Vec3d getPlayerEyePos() {
        return mc.player.getCameraPosVec(1f);
    }

    public static BlockPos getBlockStandingOnFloor() {
        return new BlockPos(
            MathHelper.floor(mc.player.getX()),
            MathHelper.floor(mc.player.getY()) - 1,
            MathHelper.floor(mc.player.getZ())
        );
    }

    public static Vec3d getNextTickPosition() {
        return mc.player.getPos().add(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
    }

    public static Vec3d getNextTickPosition(float mult) {
        return mc.player.getPos().add(
            mc.player.getVelocity().x * mult, 0, mc.player.getVelocity().z * mult);
    }

    public static Entity getEntityCuttingOtherEntity(Entity e) {
        return getEntityCuttingOtherEntity(e, entity -> true);
    }

    public static Entity getEntityCuttingOtherEntity(Entity e, Predicate<Entity> predicate) {
        List<Entity> possible = mc.world.getOtherEntities(e,
            e.getBoundingBox().expand(0.3, 2.0, 0.3),
            a -> !a.isRemoved() && !a.equals(mc.player) && predicate.test(a));
        return possible.isEmpty() ? null :
            Collections.min(possible, Comparator.comparingDouble(e2 -> e2.distanceTo(e)));
    }

    public static boolean isPlayerSuffocating() {
        Box playerBB = mc.player.getBoundingBox().shrink(0.15);
        return mc.world.getBlockCollisions(mc.player, playerBB).iterator().hasNext();
    }

    public static Direction getHorizontalFacing(float yaw) {
        return Direction.fromHorizontal(MathHelper.floor(yaw * 4f / 360f + 0.5f) & 3);
    }
}
