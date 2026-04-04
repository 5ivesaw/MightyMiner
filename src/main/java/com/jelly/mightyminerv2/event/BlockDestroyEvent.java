package com.jelly.mightyminerv2.event;

import net.minecraft.util.math.BlockPos;

public class BlockDestroyEvent extends MMEvent {
    public final BlockPos pos;
    public final int progress;
    public BlockDestroyEvent(BlockPos pos, int progress) { this.pos = pos; this.progress = progress; }
}
