// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.util.StrafeUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// From Baritone <3
// Todo: Consider Adding a rotation check To disable omnisprint from within
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    public float rotationYawHead;

    public MixinLivingEntity(World worldIn) {
        super(worldIn);
    }

    @Redirect(
            method = "jump",
            at = @At(
                    value = "FIELD",
                    target = "net/minecraft/entity/LivingEntity.getYaw():F"
            )
    )
    private float overrideYaw(LivingEntity self) {
        if (self instanceof ClientPlayerEntity && StrafeUtil.shouldEnable()) {
            return StrafeUtil.yaw;
        }
        return self.getYaw();
    }

    @Redirect(
            method = "moveEntityWithHeading",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;moveFlying(FFF)V"
            )
    )
    public void moveRelative(LivingEntity instance, float s, float f, float fr) {
        if (!StrafeUtil.shouldEnable()) {
            this.moveFlying(s, f, fr);
            return;
        }

        final float originalYaw = this.getYaw();
        this.getYaw() = StrafeUtil.yaw;

        this.moveFlying(s, f, fr);

        this.getYaw() = originalYaw;
    }
}
