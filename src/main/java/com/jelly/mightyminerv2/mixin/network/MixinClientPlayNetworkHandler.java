package com.jelly.mightyminerv2.mixin.network;

import com.jelly.mightyminerv2.event.*;
import com.jelly.mightyminerv2.util.TablistUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Unique private final List<String> mightyMiner$prevTab    = new ArrayList<>();
    @Unique private final List<String> mightyMiner$prevFooter = new ArrayList<>();

    // ── Particles ────────────────────────────────────────────────────────────
    @Inject(method = "onParticle", at = @At("HEAD"))
    private void onParticle(ParticleS2CPacket pkt, CallbackInfo ci) {
        EventBus.INSTANCE.post(new SpawnParticleEvent(
                pkt.getParameters(), pkt.getX(), pkt.getY(), pkt.getZ(),
                pkt.getOffsetX(), pkt.getOffsetY(), pkt.getOffsetZ(), pkt.getSpeed()));
    }

    // ── Block break animation ────────────────────────────────────────────────
    @Inject(method = "onBlockBreakingProgress", at = @At("HEAD"))
    private void onBlockBreak(BlockBreakingProgressS2CPacket pkt, CallbackInfo ci) {
        EventBus.INSTANCE.post(new BlockDestroyEvent(pkt.getPos(), pkt.getProgress()));
    }

    // ── Entity metadata ──────────────────────────────────────────────────────
    @Inject(method = "onEntityTrackerUpdate", at = @At("RETURN"))
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket pkt, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        Entity e = mc.world.getEntityById(pkt.id());
        if (e instanceof ArmorStandEntity && e.hasCustomName()) {
            EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) e));
        }
    }

    // ── Spawn entity ─────────────────────────────────────────────────────────
    @Inject(method = "onEntitySpawn", at = @At("TAIL"))
    private void onEntitySpawn(EntitySpawnS2CPacket pkt, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        Entity e = mc.world.getEntityById(pkt.getEntityId());
        if (e instanceof LivingEntity) {
            EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) e));
        }
    }

    // ── Tablist ──────────────────────────────────────────────────────────────
    @Inject(method = "onPlayerList", at = @At("RETURN"))
    private void onPlayerList(PlayerListS2CPacket pkt, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) return;

        Collection<PlayerListEntry> entries = mc.getNetworkHandler().getPlayerList();
        PlayerListHud hud = mc.inGameHud.getPlayerListHud();
        List<String> tablist = new ArrayList<>();

        TablistUtil.playerOrdering.sortedCopy(entries).forEach(e -> {
            Text name = e.getDisplayName();
            String s = name != null ? Formatting.strip(name.getString())
                                    : Formatting.strip(e.getProfile().getName());
            if (s != null) tablist.add(s);
        });

        if (tablist.equals(mightyMiner$prevTab)) return;
        mightyMiner$prevTab.clear();
        mightyMiner$prevTab.addAll(tablist);
        TablistUtil.setCachedTablist(tablist);
        EventBus.INSTANCE.post(new UpdateTablistEvent(tablist, System.currentTimeMillis()));
    }

    // ── Tab header/footer ────────────────────────────────────────────────────
    @Inject(method = "onPlayerListHeader", at = @At("RETURN"))
    private void onPlayerListHeader(PlayerListHeaderS2CPacket pkt, CallbackInfo ci) {
        if (pkt.footer() == null) return;
        List<String> footer = new ArrayList<>();
        for (String s : pkt.footer().getString().split("\n")) {
            String clean = Formatting.strip(s);
            if (clean != null) footer.add(clean);
        }
        if (footer.equals(mightyMiner$prevFooter)) return;
        mightyMiner$prevFooter.clear();
        mightyMiner$prevFooter.addAll(footer);
        TablistUtil.setCachedTabListFooter(footer);
        EventBus.INSTANCE.post(new UpdateTablistFooterEvent(footer));
    }
}
