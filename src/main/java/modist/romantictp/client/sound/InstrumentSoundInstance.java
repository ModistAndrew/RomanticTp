package modist.romantictp.client.sound;

import modist.romantictp.client.audio.MyChannel;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //pass message to channel and manage stop?
    public final InstrumentPlayer player;
    private CompletableFuture<ChannelAccess.ChannelHandle> channelHandle;
    @Nullable
    public Instrument instrument;
    public boolean isPlaying;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.instrument = player.getInstrument();
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
        this.instrument = player.getInstrument();
        //TODO: check instrument to stop all sound
        checkSequence();
    }

    private void checkSequence() {
        boolean isPlayingNow = player.isPlaying();
        if(isPlayingNow != isPlaying){
            if(isPlayingNow){
                InstrumentSoundManager.getInstance().playSequence(player, player.getScore());
            } else {
                InstrumentSoundManager.getInstance().stopSequence(player);
            }
        }
        isPlaying = isPlayingNow;
    }

    public void execute(Consumer<MyChannel> execution){
        this.channelHandle.thenAcceptAsync(channelHandle -> channelHandle.execute(channel -> {
            if(channel instanceof MyChannel myChannel) {
                myChannel.attachInstrument(instrument); //handle here. for sequence, handle in tick simply
                execution.accept(myChannel);
            }
        }));
    }
}