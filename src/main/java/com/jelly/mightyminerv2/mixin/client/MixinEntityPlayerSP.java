// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.event.MotionUpdateEvent;
import com.jelly.mightyminerv2.macro.MacroManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayer {

    @Unique
    float mightyMinerv2$serverYaw = 0f;
    @Unique
    float mightyMinerv2$serverPitch = 0f;
    public MixinClientPlayerEntity(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    public void onUpdateWalkingPlayerPRE(CallbackInfo ci) {
        MotionUpdateEvent event = new MotionUpdateEvent(this.getYaw(), this.getPitch());
        EventBus.INSTANCE.post(event);
        this.mightyMinerv2$serverYaw = event.yaw;
        this.mightyMinerv2$serverPitch = event.pitch;
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/ClientPlayerEntity;rotationYaw:F", opcode = Opcodes.GETFIELD))
    public float onUpdateWalkingPlayerYaw(ClientPlayerEntity instance) {
        return this.mightyMinerv2$serverYaw;
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/ClientPlayerEntity;rotationPitch:F", opcode = Opcodes.GETFIELD))
    public float onUpdateWalkingPlayerPitch(ClientPlayerEntity instance) {
        return this.mightyMinerv2$serverPitch;
    }

    @Inject(method = "dropOneItem", at = @At("HEAD"), cancellable = true)
    public void onDropOneItem(boolean dropAll, CallbackInfoReturnable<EntityItem> cir) {
        if (MacroManager.getInstance().isRunning()) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
