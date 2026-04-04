package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.event.MotionUpdateEvent;
import com.jelly.mightyminerv2.macro.MacroManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Unique private float mightyMiner$serverYaw   = 0f;
    @Unique private float mightyMiner$serverPitch = 0f;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPRE(CallbackInfo ci) {
        MotionUpdateEvent event = new MotionUpdateEvent(this.getYaw(), this.getPitch());
        EventBus.INSTANCE.post(event);
        this.mightyMiner$serverYaw   = event.yaw;
        this.mightyMiner$serverPitch = event.pitch;
    }

    // Redirect the getYaw() / getPitch() reads used when constructing the movement packet
    @Redirect(method = "sendMovementPackets",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F",
                       ordinal = 0))
    private float redirectPacketYaw(ClientPlayerEntity self) {
        return mightyMiner$serverYaw;
    }

    @Redirect(method = "sendMovementPackets",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F",
                       ordinal = 0))
    private float redirectPacketPitch(ClientPlayerEntity self) {
        return mightyMiner$serverPitch;
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfo ci) {
        if (MacroManager.getInstance().isRunning()) ci.cancel();
    }
}
