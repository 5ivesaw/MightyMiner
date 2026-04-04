// AUTO-PORTED from 1.8.9 Forge to 1.21.10 Fabric - may need manual review
package com.jelly.mightyminerv2.mixin.network;

import com.jelly.mightyminerv2.event.*;
import com.jelly.mightyminerv2.util.Logger;
import com.jelly.mightyminerv2.util.ScoreboardUtil;
import com.jelly.mightyminerv2.util.TablistUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Unique
    private final List<String> mightyMinerV2$previousTablist = new ArrayList<>();
    @Unique
    private final List<String> mightyMinerV2$previousFooter = new ArrayList<>();
    @Unique
    Map<String, SortedMap<Integer, String>> mightyMinerv2$scoreboard = new HashMap<>();
    @Unique
    String[] mightyMinerv2$objectiveNames = new String[19];

    @Inject(method = "handleParticles", at = @At(value = "HEAD"))
    public void handleParticles(S2APacketParticles packetIn, CallbackInfo ci) {
        SpawnParticleEvent event = new SpawnParticleEvent(
                packetIn.getParticleType(),
                packetIn.isLongDistance(),
                packetIn.getXCoordinate(),
                packetIn.getYCoordinate(),
                packetIn.getZCoordinate(),
                packetIn.getXOffset(),
                packetIn.getYOffset(),
                packetIn.getZOffset(),
                packetIn.getParticleArgs()
        );
        EventBus.INSTANCE.post(event);
    }

    @Inject(method = "handlePlayerListItem", at = @At(value = "RETURN"))
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn, CallbackInfo ci) {
        List<String> tablist = new ArrayList<>();
        List<NetworkPlayerInfo> players =
                TablistUtil.playerOrdering.sortedCopy(MinecraftClient.getInstance().getNetHandler().getPlayerInfoMap());

        GuiPlayerTabOverlay tabOverlay = MinecraftClient.getInstance().ingameGUI.getTabList();

        for (NetworkPlayerInfo info : players) {
            tablist.add(Formatting.strip(tabOverlay.getPlayerName(info)));
        }
        if (tablist.equals(mightyMinerV2$previousTablist)) {
            return;
        }
        mightyMinerV2$previousTablist.clear();
        mightyMinerV2$previousTablist.addAll(tablist);
        TablistUtil.setCachedTablist(tablist);
        EventBus.INSTANCE.post(new UpdateTablistEvent(tablist, System.currentTimeMillis()));
    }

    @Inject(method = "handlePlayerListHeaderFooter", at = @At("RETURN"))
    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn, CallbackInfo ci) {
        List<String> footer = new ArrayList<>();
        if (packetIn.getFooter() == null) {
            return;
        }
        for (String s : packetIn.getFooter().getString().split("\n")) {
            footer.add(Formatting.strip(s));
        }
        if (footer.equals(mightyMinerV2$previousFooter)) {
            return;
        }
        mightyMinerV2$previousFooter.clear();
        mightyMinerV2$previousFooter.addAll(footer);
        TablistUtil.setCachedTabListFooter(footer);
        EventBus.INSTANCE.post(new UpdateTablistFooterEvent(footer));
    }

//  @Inject(method = "handleSpawnObject", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
//  public void handleSpawnObject(S0EPacketSpawnObject packetIn, CallbackInfo ci, double d0, double d1, double d2, Entity entity) {
//    if (packetIn.getType() == 78) {
//      mightyMinerv2$entities.add(packetIn.getEntityID());
//    }
//  }

    @Inject(method = "handleBlockBreakAnim", at = @At("HEAD"))
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn, CallbackInfo ci) {
        EventBus.INSTANCE.post(new BlockDestroyEvent(packetIn.getPosition(), packetIn.getProgress()));
    }

    // Entity spawn stuff
    @Inject(method = "handleEntityMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/DataWatcher;updateWatchedObjectsFromList(Ljava/util/List;)V", shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn, CallbackInfo ci, Entity entity) {
        if (entity instanceof ArmorStandEntity && entity.hasCustomName()) { // right now care about stands with custom name
            EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) entity));
        }
    }

    @Inject(method = "handleSpawnMob", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleSpawnMob(S0FPacketSpawnMob packetIn, CallbackInfo ci, double d0, double d1, double d2, float f, float f1,
                               LivingEntity entitylivingbase, Entity[] aentity, List list) {
        EventBus.INSTANCE.post(new UpdateEntityEvent(entitylivingbase));
    }

    @Inject(method = "handleSpawnPlayer", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn, CallbackInfo ci, double d0, double d1, double d2, float f, float f1,
                                  EntityOtherPlayerMP entityotherplayermp, int i, List list) {
        EventBus.INSTANCE.post(new UpdateEntityEvent(entityotherplayermp));
    }

    @Redirect(method = "handleDestroyEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;removeEntityFromWorld(I)Lnet/minecraft/entity/Entity;"))
    public Entity handleDestroyEntities(WorldClient instance, int entityID) {
        Entity entity = instance.removeEntityFromWorld(entityID);
        if (entity instanceof LivingEntity) {
            EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) entity, (byte) 1));
        }
        return entity;
    }

    @Inject(method = "handleEntityMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPositionAndRotation2(DDDFFIZ)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleEntityMovement(S14PacketEntity packetIn, CallbackInfo ci, Entity entity, double d0, double d1, double d2, float f, float f1) {
        if (entity instanceof LivingEntity) {
            int nX = (int) Math.round(d0) / 3;
            int nZ = (int) Math.round(d2) / 3;
            if ((int) Math.round(entity.posX) != nX || (int) Math.round(entity.posZ) != nZ) {
                EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) entity, pack(nX, nZ)));
            }
        }
    }

    // Scoreboard
    // why the fuck did i do this (to learn how scoreboard works - wasted 1-2 day(s))
    // the other method was clearly better
    // fuck me and my autistic brain

    @Inject(method = "handleEntityTeleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S18PacketEntityTeleport;getYaw()B"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleEntityTeleport(S18PacketEntityTeleport packetIn, CallbackInfo ci, Entity entity, double d0, double d1, double d2) {
        if (entity instanceof LivingEntity) {
            int nX = (int) Math.round(d0) / 3;
            int nZ = (int) Math.round(d2) / 3;
            if ((int) Math.round(entity.posX) != nX || (int) Math.round(entity.posZ) != nZ) {
                EventBus.INSTANCE.post(new UpdateEntityEvent((LivingEntity) entity, pack(nX, nZ)));
            }
        }
    }

    @Unique
    private long pack(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    @Inject(method = "handleDisplayScoreboard", at = @At("TAIL"))
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn, CallbackInfo ci) {
        mightyMinerv2$objectiveNames[packetIn.func_149371_c()] = packetIn.func_149370_d();
        ScoreboardUtil.scoreObjNames = Arrays.copyOf(mightyMinerv2$objectiveNames, 19);
    }

    // for some reason this doesn't capture scoreobjective1
    @Inject(method = "handleScoreboardObjective", at = @At("TAIL"))
    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn, CallbackInfo ci) {
        String objName = packetIn.func_149339_c();
        SortedMap<Integer, String> removedValue = mightyMinerv2$scoreboard.remove(objName);
        if (packetIn.func_149338_e() != 1) {
            mightyMinerv2$scoreboard.put(objName, packetIn.func_149338_e() == 0 ? new TreeMap<>(Comparator.reverseOrder()) : removedValue);
        }

        String sidebarObjName = mightyMinerv2$objectiveNames[1];
        SortedMap<Integer, String> sidebar = mightyMinerv2$scoreboard.get(sidebarObjName);

        if (objName.equals(sidebarObjName) && sidebar != null) {
            EventBus.INSTANCE.post(new UpdateScoreboardEvent(new ArrayList<>(sidebar.values()), System.currentTimeMillis()));
        }

        ScoreboardUtil.scoreboard = new HashMap<>(mightyMinerv2$scoreboard);
    }

    @Inject(method = "handleUpdateScore", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getObjective(Ljava/lang/String;)Lnet/minecraft/scoreboard/ScoreboardObjective;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleUpdateScorePRE(S3CPacketUpdateScore packetIn, CallbackInfo ci, Scoreboard scoreboard) {
        try {
            if (!(packetIn.getObjectiveName())) {
                mightyMinerv2$updateScoreboard(
                        packetIn.getObjectiveName(),
                        scoreboard.getValueFromObjective(packetIn.getPlayerName(), scoreboard.getObjective(packetIn.getObjectiveName())).getScorePoints(),
                        packetIn.getScoreAction().ordinal(),
                        packetIn.getScoreValue()
                );
            } else {
                scoreboard.getObjectivesForEntity(packetIn.getPlayerName()).forEach((k, v) -> {
                    mightyMinerv2$updateScoreboard(
                            k.getName(),
                            v.getScorePoints(),
                            packetIn.getScoreAction().ordinal(),
                            packetIn.getScoreValue()
                    );
                });
            }
            String sidebarObjName = mightyMinerv2$objectiveNames[1];
            SortedMap<Integer, String> sidebar = mightyMinerv2$scoreboard.get(sidebarObjName);

            if (sidebarObjName.equals(packetIn.getObjectiveName()) && sidebar != null) {
                EventBus.INSTANCE.post(new UpdateScoreboardEvent(new ArrayList<>(sidebar.values()), System.currentTimeMillis()));
            }

            ScoreboardUtil.scoreboard = new HashMap<>(mightyMinerv2$scoreboard);
        } catch (Exception e) {
            Logger.sendNote("Couldn't Handle Update Score. Action: " + packetIn.getScoreAction());
            e.printStackTrace();
        }
    }

    @Unique
    public void mightyMinerv2$updateScoreboard(String objName, int score, int action, int packetScore) {
        SortedMap<Integer, String> objective = mightyMinerv2$scoreboard.get(objName);
        String text = objective.remove(score);
        if (text != null && action == 0) {
            objective.put(packetScore, text);
        }
    }

    @Inject(method = "handleTeams", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleTeams(S3EPacketTeams packetIn, CallbackInfo ci, Scoreboard scoreboard, Team scoreplayerteam) {
        if (scoreplayerteam == null || scoreplayerteam.getRegisteredName() == null) {
            return;
        }
        if (packetIn.getAction() == 1 || packetIn.getAction() == 4) {
            scoreplayerteam.getMembershipCollection().forEach(it -> {
                scoreboard.getObjectivesForEntity(it).forEach((a, b) -> {
                    mightyMinerv2$scoreboard.get(a.getName()).remove(b.getScorePoints());
                });
            });
        } else {
            scoreplayerteam.getMembershipCollection().forEach(it -> {
                scoreboard.getObjectivesForEntity(it).forEach((a, b) -> {
                    mightyMinerv2$scoreboard.get(a.getName()).put(b.getScorePoints(),
                            mightyMinerv2$sanitizeString(scoreplayerteam.getColorPrefix() + b.getPlayerName() + scoreplayerteam.getColorSuffix()));
                });
            });
        }

        SortedMap<Integer, String> sidebar = mightyMinerv2$scoreboard.get(mightyMinerv2$objectiveNames[1]);

        if (sidebar != null) {
            EventBus.INSTANCE.post(new UpdateScoreboardEvent(new ArrayList<>(sidebar.values()), System.currentTimeMillis()));
        }

        ScoreboardUtil.scoreboard = new HashMap<>(mightyMinerv2$scoreboard);
    }

    @Unique
    public String mightyMinerv2$sanitizeString(String scoreboard) {
        char[] arr = scoreboard.toCharArray();
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
            // required for locationtracker to work
            if (c >= 32 && c < 127 || (c == '⏣' || c == 'ф')) {
                cleaned.append(c);
            }
            if (c == 167) {
                i++;
            }
        }
        return cleaned.toString();
    }
}
