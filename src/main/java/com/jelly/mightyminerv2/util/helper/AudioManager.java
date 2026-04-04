package com.jelly.mightyminerv2.util.helper;

import com.jelly.mightyminerv2.event.EventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class AudioManager {
    private static AudioManager instance;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    /** Play a simple ping/alert sound. */
    public void playAlert() {
        mc.getSoundManager().play(PositionedSoundInstance.master(
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f));
    }

    /** Play a failure sound. */
    public void playError() {
        mc.getSoundManager().play(PositionedSoundInstance.master(
            SoundEvents.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f));
    }
}
