package com.jelly.mightyminerv2.event;

import com.jelly.mightyminerv2.util.ScoreboardUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.util.Formatting;

/**
 * Bridges Fabric's built-in events into MightyMiner's EventBus so that
 * the rest of the mod can remain largely unaware of the Fabric API.
 */
public class FabricEventBridge {
    public static void register() {
        // World load / unload
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            ScoreboardUtil.onWorldLoad();
        });

        // Chat messages
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String text = Formatting.strip(message.getString());
            if (text != null) ScoreboardUtil.onChat(text);
        });
    }
}
