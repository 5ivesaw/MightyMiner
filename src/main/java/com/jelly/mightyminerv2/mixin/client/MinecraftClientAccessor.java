package com.jelly.mightyminerv2.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("attackCooldown")
    void setAttackCooldown(int v);

    @Accessor("itemUseCooldown")
    int getItemUseCooldown();

    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int v);

    @Invoker("doAttack")
    void invokeDoAttack();

    @Invoker("doItemUse")
    void invokeDoItemUse();

    @Invoker("doItemPick")
    void invokeDoItemPick();
}
