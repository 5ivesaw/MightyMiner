package com.jelly.mightyminerv2.mixin.network;

import com.jelly.mightyminerv2.event.EventBus;
import com.jelly.mightyminerv2.event.PacketEvent;
import com.jelly.mightyminerv2.event.UpdateScoreboardLineEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Unique private final Map<Integer, String> mightyMiner$cachedSB = new HashMap<>();

    @Inject(method = "channelRead0", at = @At("HEAD"))
    private void onPacketReceived(ChannelHandlerContext ctx, Packet<?> pkt, CallbackInfo ci) {
        // Fire raw packet events
        String name = pkt.getClass().getSimpleName();
        if (name.startsWith("S") || name.contains("S2C")) {
            EventBus.INSTANCE.post(new PacketEvent.Received(pkt));
        }

        // Scoreboard line updates
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        if (pkt instanceof ScoreboardPlayerUpdateS2CPacket
                || pkt instanceof ScoreboardDisplayS2CPacket
                || pkt instanceof TeamS2CPacket) {

            Scoreboard sb = mc.player.getScoreboard();
            ScoreboardObjective obj = sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (obj == null) return;

            Collection<ScoreboardEntry> scores = sb.getScoreboardEntries(obj);
            int idx = 0;
            for (ScoreboardEntry score : scores) {
                if (score.owner().startsWith("#")) continue;
                Team team = sb.getScoreHolderTeam(score.owner());
                String formatted = team != null
                        ? Team.decorateName(team, net.minecraft.text.Text.literal(score.owner())).getString()
                        : score.owner();
                String clean = mightyMiner$clean(formatted);
                if (!clean.equals(mightyMiner$cachedSB.get(idx))) {
                    mightyMiner$cachedSB.put(idx, clean);
                    EventBus.INSTANCE.post(new UpdateScoreboardLineEvent(clean));
                }
                if (++idx > 15) break;
            }
        }
    }

    @Unique
    private String mightyMiner$clean(String s) {
        String stripped = Formatting.strip(s);
        if (stripped == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : stripped.toCharArray()) {
            if ((c >= 32 && c < 127) || c == '\u0D60') sb.append(c);
        }
        return sb.toString();
    }
}
