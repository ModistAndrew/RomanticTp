package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.audio.MyChannel;
import modist.romantictp.client.sound.audio.MidiFilter;
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
import java.util.function.Consumer;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //pass message to channel and receiver and TODO manage stop
    public final InstrumentPlayer player;
    private final MidiFilter receiver;
    private final CompletableFuture<ChannelAccess.ChannelHandle> channelHandle = new CompletableFuture<>();
    public Instrument instrument = Instrument.EMPTY;
    @Nullable
    private Sequencer sequencer;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.receiver = new MidiFilter();
        this.tick(); //init tick to update instrument, etc
    }

    public void setChannel(ChannelAccess.ChannelHandle channelHandle) { //should be called before any message is sent
        this.channelHandle.complete(channelHandle);
        this.receiver.setChannel(channelHandle);
    }

    public void sendMessage(MidiMessage message, long timeStamp) {
        this.receiver.send(message, timeStamp);
    }

    @Override
    public void tick() {
        if(!this.isStopped()) {
            this.x = player.getPos().x;
            this.y = player.getPos().y;
            this.z = player.getPos().z;
            this.volume = player.getVolume();
            updateInstrument();
            checkSequence();
            updateSequenceStatus();
            RomanticTp.info(player.isRemoved());
            if (player.isRemoved()) {
                destroy();
            }
        }
    }

    public void destroy() {
        InstrumentSoundManager.getInstance().remove(player);
        player.remove();
        closeSequencer();
        executeOnChannel(MyChannel::destroy);
        this.stop();
    }

    private void updateInstrument() {
        Instrument instrumentNow = player.getInstrument();
        if (this.instrument.equals(instrumentNow)) {
            return;
        }
        this.instrument = instrumentNow;
        this.receiver.setInstrument(this.instrument);
        executeOnChannel(myChannel -> myChannel.setReverb(this.instrument.reverb()));
    }

    private void executeOnChannel(Consumer<MyChannel> execution) {
        channelHandle.thenAcceptAsync(handle -> handle.execute(channel -> {
            if (channel instanceof MyChannel myChannel) {
                execution.accept(myChannel);
            }
        }));
    }

    private void checkSequence() {
        if (sequencer != null) {
            if (!player.isPlaying()) {
                closeSequencer();
            } else if (!sequencer.isRunning()) {
                closeSequencer();
                player.stopPlaying(); //avoid duplication
            }
        }
    }

    private void updateSequenceStatus() {
        if (this.sequencer != null) {
            this.player.updateSequenceStatus
                    ((float) sequencer.getTickPosition() / sequencer.getTickLength());
        }
    }

    private void closeSequencer() {
        if (this.sequencer != null) {
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