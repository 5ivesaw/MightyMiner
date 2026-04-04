package com.jelly.mightyminerv2.feature;

import com.jelly.mightyminerv2.feature.impl.*;
import com.jelly.mightyminerv2.feature.impl.AutoDrillRefuel.AutoDrillRefuel;
import com.jelly.mightyminerv2.feature.impl.AutoGetStats.AutoGetStats;
import com.jelly.mightyminerv2.feature.impl.AutoMobKiller.AutoMobKiller;
import com.jelly.mightyminerv2.feature.impl.BlockMiner.BlockMiner;

import java.util.List;

public class FeatureManager {
    private static FeatureManager instance;

    public final List<AbstractFeature> allFeatures = List.of(
        BlockMiner.getInstance(),
        Pathfinder.getInstance(),
        PathExecutor.getInstance(),
        WorldScanner.getInstance(),
        LagDetector.getInstance(),
        AutoMobKiller.getInstance(),
        AutoDrillRefuel.getInstance(),
        AutoGetStats.getInstance(),
        AutoCommissionClaim.getInstance(),
        AutoChestUnlocker.getInstance(),
        AutoSell.getInstance(),
        AutoWarp.getInstance(),
        RouteBuilder.getInstance(),
        RouteNavigator.getInstance(),
        MouseUngrab.getInstance()
    );

    public static FeatureManager getInstance() {
        if (instance == null) instance = new FeatureManager();
        return instance;
    }
}
