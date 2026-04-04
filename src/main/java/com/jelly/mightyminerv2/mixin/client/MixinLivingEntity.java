package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.util.StrafeUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// From Baritone
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Redirect(method = "jump",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/entity/LivingEntity;bodyYaw:F"))
    private float overrideJumpYaw(LivingEntity self) {
        if (self instanceof ClientPlayerEntity && StrafeUtil.shouldEnable()) return StrafeUtil.yaw;
        return self.bodyYaw;
    }

    @Redirect(method = "travel",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"))
    private void overrideStrafe(LivingEntity self, float speed, net.minecraft.util.math.Vec3d movVec) {
        if (!StrafeUtil.shouldEnable() || !(self instanceof ClientPlayerEntity)) {
            self.updateVelocity(speed, movVec);
            return;
        }
        float orig = self.getYaw();
        self.setYaw(StrafeUtil.yaw);
        self.updateVelocity(speed, movVec);
        self.setYaw(orig);
    }
}
