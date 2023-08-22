package modist.romantictp.client.sound.midi;

import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.instrument.Instrument;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

//Thread safety: handling audio is OK. No direct access to Render Thread.
//filter message (by instrument) to synthesizer receiver. You can send message from manager -> instance -> here or attach a transmitter
//channel will always be 1 as there is only one instrument playing at a time
public class MidiFilter implements Receiver {
    private volatile Instrument instrument = Instrument.EMPTY;
    private int lastNote = -1;
    private final Receiver innerReceiver; //synthesizer receiver

    public MidiFilter(Receiver innerReceiver) {
        this.innerReceiver = innerReceiver;
    }

    public void setInstrument(Instrument instrument) {
        if (this.instrument.equals(instrument)) {
            return;
        }
        this.instrument = instrument;
        updateInstrument();
    }

    private void updateInstrument() {
        stopAll(); //stop first
        if (!this.instrument.isEmpty()) {
            innerReceiver.send(MidiHelper.instrumentMessage(this.instrument.instrumentId()), -1); //change instrument
        }
    }

    public void stopAll() {
        for (int i = 0; i < 128; i++) {
            innerReceiver.send(MidiHelper.stopMessage(i), -1);
        }
        lastNote = -1;
        innerReceiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 123, 0), -1);
        innerReceiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 64, 0), -1);
    }

    public int getLastNote() {
        return lastNote;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage shortMessage) {
            if (this.instrument.isEmpty()) {
                return;
            }
            switch (shortMessage.getCommand()) {
                case ShortMessage.NOTE_ON -> {
                    lastNote = shortMessage.getData1(); //record note
                    innerReceiver.send(MidiHelper.startMessage(shortMessage.getData1(), shortMessage.getData2()), -1);
                }
                case ShortMessage.NOTE_OFF -> {
                    lastNote = -1; //record note
                    innerReceiver.send(MidiHelper.stopMessage(shortMessage.getData1(), shortMessage.getData2()), -1);
                }
                case ShortMessage.PROGRAM_CHANGE -> {
                    //skip instrument change
                }
                case ShortMessage.CONTROL_CHANGE -> {
                    if (shortMessage.getData1() != 0) { //skip instrument bank change
                        innerReceiver.send
                                (MidiHelper.message(shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2()), -1);
                    }
                }
                default -> innerReceiver.send
                        (MidiHelper.message(shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2()), -1);
            }
        } else {
            innerReceiver.send(message, timeStamp);
        }
    }

    @Override
    public void close() {
    }
}
