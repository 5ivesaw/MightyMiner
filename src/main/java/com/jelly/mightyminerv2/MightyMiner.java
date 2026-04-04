package com.jelly.mightyminerv2;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jelly.mightyminerv2.command.RouteBuilderCommand;
import com.jelly.mightyminerv2.config.MightyMinerConfig;
import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.event.FabricEventBridge;
import com.jelly.mightyminerv2.failsafe.FailsafeManager;
import com.jelly.mightyminerv2.feature.FeatureManager;
import com.jelly.mightyminerv2.handler.GameStateHandler;
import com.jelly.mightyminerv2.handler.GraphHandler;
import com.jelly.mightyminerv2.handler.RotationHandler;
import com.jelly.mightyminerv2.handler.RouteHandler;
import com.jelly.mightyminerv2.macro.MacroManager;
import com.jelly.mightyminerv2.util.Logger;
import com.jelly.mightyminerv2.util.ScoreboardUtil;
import com.jelly.mightyminerv2.util.TablistUtil;
import com.jelly.mightyminerv2.util.helper.AudioManager;
import com.jelly.mightyminerv2.util.helper.graph.Graph;
import com.jelly.mightyminerv2.util.helper.graph.GraphSerializer;
import com.jelly.mightyminerv2.util.helper.route.RouteWaypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MightyMiner implements ClientModInitializer {

    public static final String MOD_ID  = "mightyminerv2";
    public static final String VERSION = "2.7.3";

    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Graph<RouteWaypoint>>() {}.getType(),
                    new GraphSerializer())
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    public static final Path routesDirectory = Paths.get("./config/mightyminerv2/graphs");
    public static final Path routesFile      = Paths.get("./config/mightyminerv2/routes.json");

    private static final List<String> EXPECTED_ROUTES =
            Arrays.asList("Glacial Macro.json", "Commission Macro.json");

    public static MightyMinerConfig config;

    @Override
    public void onInitializeClient() {
        // Config
        config = new MightyMinerConfig();
        config.load();

        // Routes / graphs
        initRoutes();

        // Register Fabric event bridges
        FabricEventBridge.register();

        // Register all listeners into our EventBus
        EventBus.INSTANCE.register(GameStateHandler.getInstance());
        EventBus.INSTANCE.register(RotationHandler.getInstance());
        EventBus.INSTANCE.register(RouteHandler.getInstance());
        EventBus.INSTANCE.register(GraphHandler.instance);
        EventBus.INSTANCE.register(MacroManager.getInstance());
        EventBus.INSTANCE.register(FailsafeManager.getInstance());
        EventBus.INSTANCE.register(AudioManager.getInstance());
        FeatureManager.getInstance().allFeatures.forEach(EventBus.INSTANCE::register);
        EventBus.INSTANCE.register(new ScoreboardUtil());
        EventBus.INSTANCE.register(new TablistUtil());

        // Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                RouteBuilderCommand.register(dispatcher));

        // Apply settings on world join
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            client.options.getGamma().setValue(1000.0);
            client.options.pauseOnLostFocus = false;
            if (!MightyMinerConfig.selectedRoute.isEmpty())
                RouteHandler.getInstance().selectRoute(MightyMinerConfig.selectedRoute);
        });
    }

    private void initRoutes() {
        File dir = routesDirectory.toFile();
        if (!dir.exists()) dir.mkdirs();

        File[] files = dir.listFiles();
        boolean needsReset = files == null || files.length != EXPECTED_ROUTES.size();

        if (needsReset && files != null) {
            for (File f : files) {
                try { Files.deleteIfExists(f.toPath()); } catch (Exception e) { e.printStackTrace(); }
            }
        }

        if (needsReset) {
            for (String name : EXPECTED_ROUTES) {
                Path dest = routesDirectory.resolve(name);
                try (InputStream is = MightyMiner.class.getResourceAsStream("/mightyminer/" + name)) {
                    if (is != null) Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) { System.out.println("Failed to copy " + name); continue; }
                loadGraph(dest);
            }
            return;
        }

        for (File f : Objects.requireNonNull(files)) {
            if (f.isFile() && EXPECTED_ROUTES.contains(f.getName())) loadGraph(f.toPath());
        }
        RouteHandler.getInstance().loadData();
    }

    private void loadGraph(Path path) {
        String key = path.getFileName().toString().replace(".json", "");
        try (Reader r = Files.newBufferedReader(path)) {
            Graph<RouteWaypoint> g = gson.fromJson(r,
                    new TypeToken<Graph<RouteWaypoint>>() {}.getType());
            GraphHandler.instance.graphs.put(key, g);
        } catch (Exception e) {
            System.out.println("Couldn't load graph: " + key);
            e.printStackTrace();
        }
    }
}
