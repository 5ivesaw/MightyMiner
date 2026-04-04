package com.jelly.mightyminerv2.util;

import com.jelly.mightyminerv2.mixin.client.MinecraftAccessor;
import com.jelly.mightyminerv2.util.helper.Angle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AngleUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final double randomAddition = (Math.random() * 0.3 - 0.15);

    public static Angle getPlayerAngle() {
        return new Angle(get360RotationYaw(), mc.player.getPitch());
    }

    public static float get360RotationYaw(float yaw) {
        return (yaw % 360 + 360) % 360;
    }

    // This is MathHelper::wrapAngleTo180_float
    public static float normalizeAngle(float yaw) {
        float newYaw = yaw % 360F;
        if (newYaw < -180F) {
            newYaw += 360F;
        }
        if (newYaw > 180F) {
            newYaw -= 360F;
        }
        return newYaw;
    }

    public static float get360RotationYaw() {
        if (mc.thePlayer == null) {
            return 0;
        }
        return get360RotationYaw(mc.player.getYaw());
    }

    public static float clockwiseDifference(float initialYaw360, float targetYaw360) {
        return get360RotationYaw(targetYaw360 - initialYaw360);
    }

    public static float antiClockwiseDifference(float initialYaw360, float targetYaw360) {
        return get360RotationYaw(initialYaw360 - targetYaw360);
    }

    public static float smallestAngleDifference(float initialYaw360, float targetYaw360) {
        return Math.min(clockwiseDifference(initialYaw360, targetYaw360), antiClockwiseDifference(initialYaw360, targetYaw360));
    }

    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d(f1 * f2, f3, f * f2);
    }

    public static Vec3d getVectorForRotation(float yaw) {
        return new Vec3d(-MathHelper.sin(-yaw * 0.017453292F - 3.1415927F), 0, -MathHelper.cos(-yaw * 0.017453292F - 3.1415927F));
    }

    public static Angle getRotation(Vec3d to) {
        return getRotation(mc.thePlayer.getPositionEyes(((MinecraftAccessor) mc).getTimer().renderPartialTicks), to);
    }

    public static Angle getRotation(Entity to) {
        return getRotation(mc.thePlayer.getPositionEyes(((MinecraftAccessor) mc).getTimer().renderPartialTicks),
                to.getPositionVector().addVector(0, Math.min(((to.height * 0.85) + randomAddition), 1.7), 0));
    }

    public static Angle getRotation(BlockPos pos) {
        return getRotation(mc.thePlayer.getPositionEyes(((MinecraftAccessor) mc).getTimer().renderPartialTicks), new Vec3d(pos).addVector(0.5, 0.5, 0.5));
    }

    public static Angle getRotation(Vec3d from, BlockPos pos) {
        return getRotation(from, new Vec3d(pos).addVector(0.5, 0.5, 0.5));
    }

    public static Angle getRotation(Vec3d from, Vec3d to) {
        double xDiff = to.x - from.x;
        double yDiff = to.y - from.y;
        double zDiff = to.z - from.z;

        double dist = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) Math.toDegrees(Math.atan2(zDiff, xDiff)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(yDiff, dist));

//    if (randomness) {
//      yaw += (float) ((Math.random() - 1) * 4);
//      pitch += (float) ((Math.random() - 1) * 4);
//    }

        return new Angle(yaw, pitch);
    }

    public static float getRotationYaw(Vec3d to) {
        return (float) -Math.toDegrees(Math.atan2(to.x - mc.player.getX(), to.z - mc.player.getZ()));
    }

    public static float getRotationYaw(Vec3d from, Vec3d to) {
        return (float) -Math.toDegrees(Math.atan2(to.x - from.x, to.z - from.z));
    }

    public static float getRotationYaw360(Vec3d to) {
        float yaw = (float) -Math.toDegrees(Math.atan2(to.x - mc.player.getX(), to.z - mc.player.getZ()));
        if (yaw < 0) {
            return yaw + 360.0f;
        }
        return yaw;
    }

    public static float getRotationYaw360(Vec3d from, Vec3d to) {
        float yaw = (float) -Math.toDegrees(Math.atan2(to.x - from.x, to.z - from.z));
        if (yaw < 0) {
            return yaw + 360.0f;
        }
        return yaw;
    }

    // start and end should be normalized;
    public static float getNeededYawChange(float start, float end) {
        return normalizeAngle(end - start);
    }

    // Todo: More testing
    // edit this prolly bad pls fix this
    public static Angle getNeededChange(Angle startAngle, Angle endAngle) {
        float yawChange = normalizeAngle(normalizeAngle(endAngle.getYaw()) - normalizeAngle(startAngle.getYaw()));
        return new Angle(yawChange, endAngle.getPitch() - startAngle.getPitch());
    }

    public static boolean isLookingAtDebug(Vec3d vec, float distance) {
        System.out.println("PlayerAngle: " + getPlayerAngle());
        System.out.println("RotationForVec: " + getRotation(vec));
        Angle change = getNeededChange(getPlayerAngle(), getRotation(vec));
        System.out.println("Change: " + change + ", Dist: " + distance);
        return Math.abs(change.getYaw()) <= distance && Math.abs(change.getPitch()) <= distance;
    }

    public static boolean isLookingAt(Vec3d vec, float distance) {
        Angle change = getNeededChange(getPlayerAngle(), getRotation(vec));
        return Math.abs(change.getYaw()) <= distance && Math.abs(change.getPitch()) <= distance;
    }
}
