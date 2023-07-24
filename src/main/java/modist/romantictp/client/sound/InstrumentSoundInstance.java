package modist.romantictp.client.sound;

import modist.romantictp.client.sound.audio.OuterReceiver;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.concurrent.CompletableFuture;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //pass message to channel and receiver and manage stop?
    public final InstrumentPlayer player;
    private final OuterReceiver receiver;
    private final CompletableFuture<ChannelAccess.ChannelHandle> channelHandle = new CompletableFuture<>();
    public Instrument instrument = Instrument.EMPTY;
    @Nullable
    private Sequencer sequencer;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.receiver = new OuterReceiver();
        this.tick();
    }

    public void setChannel(ChannelAccess.ChannelHandle channelHandle) { //should be called before any message is sent
        this.channelHandle.complete(channelHandle);
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
        updateInstrument();
        checkSequence();
    }

    private void updateInstrument() {
        Instrument instrumentNow = player.getInstrument();
        if(this.instrument.equals(instrumentNow)){
            return;
        }
        this.instrument = instrumentNow;
        this.receiver.setInstrument(this.instrument);
    }

    private void checkSequence() {
        if(!player.isPlaying() && this.sequencer !=null){
            closeSequencer();
        }
    }

    private void closeSequencer() {
        if(this.sequencer!=null){
            sequencer.close(); //this will call the receiver to stop all notes
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