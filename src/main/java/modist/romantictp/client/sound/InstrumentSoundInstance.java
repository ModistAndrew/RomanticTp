package modist.romantictp.client.sound;

import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //TODO pass message to channel and manage stop!
    public final InstrumentPlayer player;
    private CompletableFuture<ChannelAccess.ChannelHandle> channelHandle;
    @Nullable
    public Instrument activeInstrument;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
    }

    public void setChannel(CompletableFuture<ChannelAccess.ChannelHandle> channelHandle) {
        this.channelHandle = channelHandle;
    }

    public CompletableFuture<ChannelAccess.ChannelHandle> getChannel() {
        return this.channelHandle;
    }
    @Override
    public void tick() {
        this.x = player.getPos().x;
        this.y = player.getPos().y;
        this.z = player.getPos().z;
        this.volume = player.getVolume();
        if(activeInstrument!=null && !activeInstrument.equals(player.getActiveInstrument())) {
            //TODO: change instrument send stop midi message? sequence stop?
            InstrumentSoundManager.getInstance().stopSequence(player);
        }
    }

}