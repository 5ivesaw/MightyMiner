package com.jelly.mightyminerv2.mixin.render;

import com.jelly.mightyminerv2.handler.RotationHandler;
import com.jelly.mightyminerv2.mixin.client.ClientPlayerEntityAccessor;
import com.jelly.mightyminerv2.mixin.client.MinecraftClientAccessor;
import com.jelly.mightyminerv2.util.AngleUtil;
import com.jelly.mightyminerv2.util.helper.RotationConfiguration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BipedEntityModel.class, priority = Integer.MAX_VALUE)
public class MixinBipedEntityModel<T extends net.minecraft.entity.LivingEntity> {

    @Shadow public ModelPart head;
    @Unique private final MinecraftClient mightyMiner$mc = MinecraftClient.getInstance();

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void onSetAngles(T entity, float limbAngle, float limbDistance,
                              float animProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!RotationHandler.getInstance().isEnabled()) return;
        if (RotationHandler.getInstance().getConfiguration() == null) return;
        if (RotationHandler.getInstance().getConfiguration().rotationType()
                != RotationConfiguration.RotationType.SERVER) return;
        if (entity == null || !entity.equals(mightyMiner$mc.player)) return;

        this.head.pitch = ((ClientPlayerEntityAccessor) entity).getLastPitch() / 57.295776f;
        float yawOffset = entity.bodyYaw + AngleUtil.normalizeAngle(
                entity.bodyYaw - entity.prevBodyYaw) * mightyMiner$mc.getRenderTickCounter().getLastFrameDuration();
        float netHead = MathHelper.wrapDegrees(
                ((ClientPlayerEntityAccessor) entity).getLastYaw() - yawOffset);
        this.head.yaw = netHead / 57.295776f;
    }
}
