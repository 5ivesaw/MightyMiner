package com.jelly.mightyminerv2.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class RaytracingUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean canSeePoint(Vec3d point) {
        return canSeePoint(PlayerUtil.getPlayerEyePos(), point);
    }

    public static boolean canSeePoint(Vec3d from, Vec3d point) {
        HitResult result = raytrace(from, point);
        if (result == null || result.getType() == HitResult.Type.MISS) return true;
        Vec3d hit = result.getPos();
        return Math.abs(hit.x - point.x) < 0.1
            && Math.abs(hit.y - point.y) < 0.1
            && Math.abs(hit.z - point.z) < 0.1;
    }

    public static HitResult raytraceTowards(Vec3d v1, Vec3d v2, double dist) {
        Vec3d norm = v2.subtract(v1).normalize();
        return raytrace(v1, v1.add(norm.multiply(dist)));
    }

    public static HitResult raytrace(Vec3d v1, Vec3d v2) {
        if (mc.world == null) return null;
        // Check entities first
        Vec3d delta = v2.subtract(v1);
        List<Entity> entities = mc.world.getOtherEntities(mc.player,
            mc.player.getBoundingBox().stretch(delta).expand(1, 1, 1),
            e -> e.isAlive() && e.isCollidable());
        for (Entity e : entities) {
            var intercept = e.getBoundingBox().expand(0.5).raycast(v1, v2);
            if (intercept.isPresent()) {
                // Return a block hit at entity pos as approximation
                return mc.world.raycast(new RaycastContext(v1, intercept.get(),
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            }
        }
        return mc.world.raycast(new RaycastContext(v1, v2,
            RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
    }
}
