package modist.romantictp.client.sound;

import modist.romantictp.client.audio.OuterReceiver;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.concurrent.CompletableFuture;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //pass message to channel and manage stop?
    public final InstrumentPlayer player;
    @Nullable
    public Instrument instrument;
    private final OuterReceiver receiver;
    private CompletableFuture<ChannelAccess.ChannelHandle> channelHandle;
    @Nullable
    private Sequencer sequencer;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.instrument = player.getInstrument();
        this.receiver = new OuterReceiver(player);
    }

    public void setChannel(CompletableFuture<ChannelAccess.ChannelHandle> channelHandle) { //should be called before any message is sent
        this.channelHandle = channelHandle;
        this.receiver.setChannel(channelHandle);
    }

    public void sendMessage(MidiMessage message, long timeStamp) {
        this.receiver.send(message,timeStamp);
    }

    @Override
    public void tick() {
        this.x = player.getPos().x;
        this.y = player.getPos().y;
        this.z = player.getPos().z;
        this.volume = player.getVolume();
        this.instrument = player.getInstrument();
        //TODO: check instrument to stop all sound and sequence
        checkSequence();
    }

    private void checkSequence() {
        if(!player.isPlaying() && this.sequencer !=null){
            closeSequencer();
        }
    }

    private void closeSequencer() {
        if(this.sequencer!=null){
            sequencer.close();
        }
        this.sequencer = null;
    }

    public void attachSequencer(Sequence sequence) {
        closeSequencer();
        try {
            this.sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.setSequence(sequence);
            sequencer.getTransmitter().setReceiver(receiver);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
}