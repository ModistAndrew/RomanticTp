package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.audio.MidiFilter;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.common.item.ItemLoader;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
            this.pitch = (90F - player.getXRot()) / 180F;
            if(!player.getUseItem().is(ItemLoader.NATURAL_TRUMPET.get())){
                this.stop();
            }
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}