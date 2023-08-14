package modist.romantictp.client.sound;

import modist.romantictp.client.event.ClientEventHandler;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.audio.MidiFilter;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //interact with channel and receiver and manage stop
    public final InstrumentPlayer player;
    private final MidiFilter receiver;
    private final CompletableFuture<ChannelAccess.ChannelHandle> channelHandle = new CompletableFuture<>();
    public Instrument instrument = Instrument.EMPTY;
    private boolean hasReverbHelmet;
    @Nullable
    private Sequencer sequencer;
    private int lastNote = -1;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.receiver = new MidiFilter();
        this.tick(); //init tick to update instrument, etc
    }

    public void setChannel(ChannelAccess.ChannelHandle channelHandle) { //should be called before any message is sent
        this.channelHandle.complete(channelHandle);
        this.receiver.setChannel(channelHandle);
        executeOnChannel(myChannel -> myChannel.bindInstance(this));
    }

    public void sendMessage(MidiMessage message, long timeStamp) {
        this.receiver.send(message, timeStamp);
    }

    @Override
    public void tick() {
        if (!this.isStopped()) {
            this.x = player.getPos().x;
            this.y = player.getPos().y;
            this.z = player.getPos().z;
            this.volume = player.getVolume();
            generateParticle();
            updateInstrument();
            checkSequence();
            if ((channelHandle.isDone() && channelHandle.join().isStopped()) || player.isRemoved()) {
                destroy();
            }
        }
    }

    private void generateParticle() {
        if(this.lastNote != receiver.getLastNote()){
            this.lastNote = receiver.getLastNote();
            if(lastNote >= 0) {
                player.addParticle(lastNote);
            }
        }
    }

    public void destroy() { //outer or inner
        InstrumentSoundManager.getInstance().remove(player);
        InstrumentPlayerManager.remove(player);
        closeSequencer();
        executeOnChannel(MyChannel::destroy);
        this.stop();
    }

    private void updateInstrument() {
        Instrument instrumentNow = player.getInstrument();
        boolean hasReverbHelmetNow = ClientEventHandler.hasReverbHelmet();
        if (this.instrument.equals(instrumentNow) && hasReverbHelmetNow == hasReverbHelmet) {
            return;
        }
        this.instrument = instrumentNow;
        this.hasReverbHelmet = hasReverbHelmetNow;
        this.receiver.setInstrument(this.instrument);
        executeOnChannel(myChannel -> myChannel.setReverb(hasReverbHelmet ? ReverbType.CONCERT_HALL : this.instrument.reverb()));
        //reverb helmet can override instrument reverb
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
            if (!player.isPlaying() || sequencer.getTickPosition() == sequencer.getTickLength()) {
                closeSequencer();
            }
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

    public void pause() {
        if(sequencer != null) {
            sequencer.stop();
        }
        receiver.setInstrument(this.instrument); //stop all sounds
    }

    public void unpause() {
        if(sequencer != null) {
            sequencer.start();
        }
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }
}