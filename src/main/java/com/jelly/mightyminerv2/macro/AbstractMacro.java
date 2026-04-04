package com.jelly.mightyminerv2.macro;

import com.jelly.mightyminerv2.util.Logger;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public abstract class AbstractMacro extends Logger {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    protected boolean running = false;

    public boolean isRunning() { return running; }

    public void start() {
        running = true;
        ClientTickEvents.END_CLIENT_TICK.register(client -> onTick());
    }

    public void stop() { running = false; }

    /** Called every client tick while running. */
    protected void onTick() {}

    @Override
    public String getName() { return getClass().getSimpleName(); }
}
