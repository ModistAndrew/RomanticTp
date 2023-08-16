package modist.romantictp.client.sound;

import modist.romantictp.client.event.ClientEventHandler;
import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.audio.MidiFilter;
import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.util.function.Consumer;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    //interact with channel and receiver and manage stop
    public final InstrumentPlayer player;
    private final MidiFilter midiFilter;
    public final SynthesizerPool.SynthesizerWrapper synthesizerWrapper;
    @Nullable
    private Sequencer sequencer;
    public Instrument instrument = Instrument.EMPTY;
    private boolean hasReverbHelmet;

    private int lastNote = -1;

    public InstrumentSoundInstance(InstrumentPlayer player) {
        super(SoundEventLoader.BLANK.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.synthesizerWrapper = SynthesizerPool.getInstance().request(this);
        this.synthesizerWrapper.bindInstance(this);
        this.midiFilter = new MidiFilter(synthesizerWrapper.receiver);
        this.tick(); //init tick to update instrument, etc
    }

    public void sendMessage(MidiMessage message, long timeStamp) {
        this.midiFilter.send(message, timeStamp);
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
            if (player.isRemoved()) {
                destroy();
            }
        }
    }

    private void generateParticle() {
        if(this.lastNote != midiFilter.getLastNote()){
            this.lastNote = midiFilter.getLastNote();
            if(lastNote >= 0) {
                player.addParticle(lastNote);
            }
        }
    }

    public void destroy() { //stop sound or player removed
        InstrumentSoundManager.getInstance().remove(player);
        InstrumentPlayerManager.remove(player);
        closeSequencer();
        SynthesizerPool.getInstance().delete(this);
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
        this.midiFilter.setInstrument(this.instrument);
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
            sequencer.getTransmitter().setReceiver(midiFilter);
            sequencer.start();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void pause() {
        if(sequencer != null) {
            sequencer.stop();
        }
        midiFilter.updateInstrument(); //stop all sounds
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