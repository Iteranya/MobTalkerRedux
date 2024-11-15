package org.arsparadox.mobtalkerredux.vn.view;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class SoundUtils {

    private SimpleSoundInstance currentMusic = null;
    private SimpleSoundInstance currentSound = null;

    /**
     * Plays a sound effect once at full volume
     * Stops any currently playing sound effect
     * @param sound The ResourceLocation of the sound to play
     */
    public void playSound(ResourceLocation sound) {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
        }
        currentSound = SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvent.createVariableRangeEvent(sound),
                1.0F, // Pitch
                1.5F  // Volume
        );
        Minecraft.getInstance().getSoundManager().play(currentSound);
    }

    /**
     * Plays a sound effect with custom volume and pitch
     * Stops any currently playing sound effect
     * @param sound The ResourceLocation of the sound to play
     * @param volume Volume from 0.0 to 1.0
     * @param pitch Pitch from 0.5 to 2.0
     */
    public void playSound(ResourceLocation sound, float volume, float pitch) {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
        }
        currentSound = SimpleSoundInstance.forUI(
                net.minecraft.sounds.SoundEvent.createVariableRangeEvent(sound),
                volume,
                pitch
        );
        Minecraft.getInstance().getSoundManager().play(currentSound);
    }

    /**
     * Plays music that will loop continuously
     * Automatically stops any currently playing music
     * @param music The ResourceLocation of the music to play
     */
    public void playMusic(ResourceLocation music) {
        stopMusic();
        currentMusic = new SimpleSoundInstance(
                music,
                SoundSource.MUSIC,
                1.0F, // Volume
                1.0F, // Pitch
                SoundInstance.createUnseededRandom(),
                true, // Loop
                0,    // Delay
                SoundInstance.Attenuation.NONE,
                0.0D, // x
                0.0D, // y
                0.0D, // z
                true  // Relative
        );
        Minecraft.getInstance().getSoundManager().play(currentMusic);
    }

    /**
     * Plays music with custom volume that will loop continuously
     * Automatically stops any currently playing music
     * @param music The ResourceLocation of the music to play
     * @param volume Volume from 0.0 to 1.0
     */
    public void playMusic(ResourceLocation music, float volume) {
        stopMusic();
        currentMusic = new SimpleSoundInstance(
                music,
                SoundSource.MUSIC,
                volume,
                1.0F,
                SoundInstance.createUnseededRandom(),
                true,
                0,
                SoundInstance.Attenuation.NONE,
                0.0D,
                0.0D,
                0.0D,
                true
        );
        Minecraft.getInstance().getSoundManager().play(currentMusic);
    }

    /**
     * Stops any currently playing music
     */
    public void stopMusic() {
        if (currentMusic != null) {
            Minecraft.getInstance().getSoundManager().stop(currentMusic);
            currentMusic = null;
        }
    }

    /**
     * Stops any currently playing sound effect
     */
    public void stopSound() {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
    }
}