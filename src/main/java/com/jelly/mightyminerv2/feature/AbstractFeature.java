package com.jelly.mightyminerv2.feature;

import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Base class for all MightyMiner features – ported from Forge to Fabric 1.21.10.
 */
public abstract class AbstractFeature extends Logger {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    protected boolean enabled = false;

    public boolean isRunning() { return enabled; }

    /** Called once when the feature starts its main loop. Subclasses override as needed. */
    protected void start() {}

    /** Stop the feature. Subclasses should call super.stop() at the end. */
    public void stop() {
        this.enabled = false;
    }

    @Override
    public String getName() { return getClass().getSimpleName(); }
}
