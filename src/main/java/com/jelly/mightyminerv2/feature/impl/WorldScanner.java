// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.feature.impl;

import com.jelly.mightyminerv2.feature.AbstractFeature;
import lombok.Getter;

public class WorldScanner extends AbstractFeature {

    @Getter
    public static WorldScanner instance = new WorldScanner();

    @Override
    public String getName() {
        return "WorldScanner";
    }

}
