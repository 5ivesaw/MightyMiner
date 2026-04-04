package com.jelly.mightyminerv2.util;

import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.event.UpdateScoreboardLineEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Pattern COLD_REGEX = Pattern.compile("Cold: -?(\\d{1,3})");
    public static int cold = 0;

    /** Returns the current sidebar scoreboard lines as a list of strings (no colour codes). */
    public static List<String> getScoreboard() {
        try {
            if (mc.world == null) return Collections.emptyList();
            Scoreboard sb = mc.world.getScoreboard();
            ScoreboardObjective obj = sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (obj == null) return Collections.emptyList();
            List<String> result = new ArrayList<>();
            for (ScoreboardEntry e : sb.getScoreboardEntries(obj)) {
                if (e.owner().startsWith("#")) continue;
                Team team = sb.getScoreHolderTeam(e.owner());
                String formatted = team != null
                        ? Team.decorateName(team, Text.literal(e.owner())).getString()
                        : e.owner();
                result.add(sanitizeString(formatted));
            }
            return result;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    public static String getScoreboardTitle() {
        if (mc.world == null) return "";
        Scoreboard sb = mc.world.getScoreboard();
        if (sb == null) return "";
        ScoreboardObjective obj = sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (obj == null) return "";
        return sanitizeString(obj.getDisplayName().getString());
    }

    public static String sanitizeString(String s) {
        String stripped = Formatting.strip(s);
        if (stripped == null) return "";
        StringBuilder out = new StringBuilder();
        for (char c : stripped.toCharArray()) {
            if (c >= 32 && c < 127) out.append(c);
        }
        return out.toString();
    }

    // Called from FabricEventBridge on world load
    public static void onWorldLoad() { cold = 0; }

    // Called from FabricEventBridge on chat
    public static void onChat(String message) {
        if (message.contains("The warmth of the campfire reduced your") && message.contains("Cold")) {
            cold = 0;
        }
    }

    @EventBus.SubscribeEvent
    public void onScoreboardLine(UpdateScoreboardLineEvent event) {
        String line = event.getLine();
        if (line.contains("Cold:")) {
            Matcher m = COLD_REGEX.matcher(line);
            cold = m.find() ? Integer.parseInt(m.group(1)) : 0;
        }
        List<String> lines = getScoreboard();
        if (lines.stream().noneMatch(l -> sanitizeString(l).contains("Cold:"))) cold = 0;
    }
}
