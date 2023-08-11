package modist.romantictp.client.sound;

import modist.romantictp.common.item.ItemLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class NaturalTrumpetSoundInstance extends AbstractTickableSoundInstance {
    private final LivingEntity player;

    public NaturalTrumpetSoundInstance(LivingEntity player) {
        super(SoundEventLoader.NATURAL_TRUMPET.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
    }

    @Override
    public void tick() {
        if (!this.isStopped()) {
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
            this.pitch = getPitch(-player.getXRot() / 90F);
            if(!player.getUseItem().is(ItemLoader.NATURAL_TRUMPET.get())){
                this.stop();
            }
        }
    }

    private float getPitch(float scale) {
        return (float) Math.pow(2, scale);
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}