package modist.romantictp.client.audio;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.instrument.Instrument;
import modist.romantictp.client.instrument.InstrumentPlayer;

import javax.sound.midi.*;

public class MidiFilter implements Receiver {
    private final Receiver receiver;
    private final InstrumentPlayer player;

    public MidiFilter(Receiver receiver, InstrumentPlayer player) {
        this.receiver = receiver;
        this.player = player;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        RomanticTp.info("receive" + System.currentTimeMillis());
        Instrument instrument = player.getInstrument();
        if(instrument == null){
            return;
        }
        try {
            receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, instrument.instrumentId, 0), -1);
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
