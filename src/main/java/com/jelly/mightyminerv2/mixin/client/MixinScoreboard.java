package com.jelly.mightyminerv2.mixin.client;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Map;

/** Patcher-style null-safety fixes for scoreboard log spam. */
@Mixin(Scoreboard.class)
public abstract class MixinScoreboard {
    @Shadow public abstract Team getTeam(String name);

    @Inject(method = "removeTeam", at = @At("HEAD"), cancellable = true)
    private void patcher$nullTeam(Team team, CallbackInfo ci) { if (team == null) ci.cancel(); }

    @Redirect(method = "removeTeam", at = @At(value = "INVOKE",
              target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;",
              ordinal = 0, remap = false))
    private <K,V> V patcher$safeRemoveTeam(Map<K,V> map, K key) {
        return key != null ? map.remove(key) : null;
    }

    @Inject(method = "removeObjective", at = @At("HEAD"), cancellable = true)
    private void patcher$nullObjective(ScoreboardObjective obj, CallbackInfo ci) { if (obj == null) ci.cancel(); }

    @Redirect(method = "removeObjective", at = @At(value = "INVOKE",
              target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;",
              ordinal = 0, remap = false))
    private <K,V> V patcher$safeRemoveObj(Map<K,V> map, K key) {
        return key != null ? map.remove(key) : null;
    }

    @Inject(method = "addTeam", at = @At(value = "CONSTANT",
              args = "stringValue=A team with the name '"), cancellable = true)
    private void patcher$returnExisting(String name, CallbackInfoReturnable<Team> cir) {
        cir.setReturnValue(getTeam(name));
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)V",
            at = @At(value = "CONSTANT",
              args = "stringValue=Player is either on another team or not on any team. Cannot remove from team '"),
            cancellable = true)
    private void patcher$silenceException(CallbackInfo ci) { ci.cancel(); }
}
