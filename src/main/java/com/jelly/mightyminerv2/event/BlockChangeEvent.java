package com.jelly.mightyminerv2.event;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockChangeEvent extends MMEvent {
    public final BlockPos pos;
    public final BlockState oldState;
    public final BlockState newState;
    public BlockChangeEvent(BlockPos pos, BlockState old, BlockState state) {
        this.pos = pos; this.oldState = old; this.newState = state;
    }
}
