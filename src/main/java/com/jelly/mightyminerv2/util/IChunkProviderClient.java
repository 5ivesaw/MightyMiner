package com.jelly.mightyminerv2.util;

import net.minecraft.world.chunk.WorldChunk;
import java.util.List;

/**
 * Stub interface for 1.21 – chunk access via ClientWorld.getChunk() is preferred.
 */
public interface IChunkProviderClient {
    List<WorldChunk> chunkListing();
}
