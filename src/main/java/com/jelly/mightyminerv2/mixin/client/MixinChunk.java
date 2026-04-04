// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.client;

import com.jelly.mightyminerv2.event.BlockChangeEvent;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chunk.class)
public class MixinChunk {
    @Inject(method = "setBlockState", at = @At("RETURN"))
    public void setBlockState(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir) {
        final BlockState old = cir.getReturnValue();
        if (old == null || state == old) return;
        EventBus.INSTANCE.post(new BlockChangeEvent(pos, old, state));
    }
}