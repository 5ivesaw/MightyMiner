package com.jelly.mightyminerv2.util;

import com.google.common.collect.Ordering;
import net.minecraft.client.network.PlayerListEntry;
import java.util.*;

public class TablistUtil {
    private static List<String> cachedTablist = new ArrayList<>();
    private static List<String> cachedTabListFooter = new ArrayList<>();

    public static final Ordering<PlayerListEntry> playerOrdering = Ordering.from((a, b) -> {
        // Sort by name for consistency
        String na = a.getProfile().getName();
        String nb = b.getProfile().getName();
        return na.compareToIgnoreCase(nb);
    });

    public static void setCachedTablist(List<String> list)       { cachedTablist = new ArrayList<>(list); }
    public static void setCachedTabListFooter(List<String> list) { cachedTabListFooter = new ArrayList<>(list); }
    public static List<String> getCachedTablist()                { return Collections.unmodifiableList(cachedTablist); }
    public static List<String> getCachedTabListFooter()          { return Collections.unmodifiableList(cachedTabListFooter); }
}
