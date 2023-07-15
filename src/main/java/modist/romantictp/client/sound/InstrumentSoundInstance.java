package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.provider.IMusicProvider;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public abstract class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    private final IMusicProvider musicProvider;
    private final Minecraft mc;
    public InstrumentSoundInstance(LivingEntity player, SoundEvent pSoundEvent, SoundSource pSource, IMusicProvider musicProvider) {
        super(pSoundEvent, pSource, SoundInstance.createUnseededRandom());
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.musicProvider = musicProvider;
        this.mc = Minecraft.getInstance();
        this.looping = true;
    }

    @Override
    public void tick() {
        if(checkStop()) {
            this.stop();
            return;
        }
        this.pitch = musicProvider.getPitch();
        this.volume = musicProvider.getVolume();
    }

    protected abstract boolean checkStop();
}
