package com.jelly.mightyminerv2.command;

import com.jelly.mightyminerv2.feature.impl.RouteBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class RouteBuilderCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("routebuilder")
            .executes(ctx -> {
                RouteBuilder.getInstance().toggle();
                return 1;
            })
        );
    }
}
