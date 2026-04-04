// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Accessor
    float getLastReportedYaw();

    @Accessor
    float getLastReportedPitch();
}
