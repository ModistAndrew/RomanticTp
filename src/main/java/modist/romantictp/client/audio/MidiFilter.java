package modist.romantictp.client.audio;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;

import javax.annotation.Nullable;
import javax.sound.midi.*;

public class MidiFilter implements Receiver {
    private final Receiver receiver;
    @Nullable
    private Instrument activeInstrument;

    public MidiFilter(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setActiveInstrument(Instrument instrument){
        activeInstrument = instrument;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) { //TODO: store last note
        RomanticTp.info("receive" + System.currentTimeMillis());
        if(activeInstrument == null){
            return;
        }
        try {
            receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, activeInstrument.instrumentId, 0), -1);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        receiver.send(message, timeStamp);
    }

    @Override
    public void close() {
        receiver.close();
    }
}
