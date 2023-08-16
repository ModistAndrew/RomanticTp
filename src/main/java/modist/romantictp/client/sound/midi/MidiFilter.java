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

    public int getLastNote() {
        return lastNote;
    }

    public void updateInstrument() { //first stop
        for (int i = 0; i < 128; i++) {
            innerReceiver.send(MidiHelper.stopMessage(i), -1);
        }
        lastNote = -1;
        innerReceiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 123, 0), -1);
        innerReceiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 64, 0), -1);
        if (!this.instrument.isEmpty()) {
            innerReceiver.send(MidiHelper.instrumentMessage(this.instrument.instrumentId()), -1);
        }
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage shortMessage) {
            if (this.instrument.isEmpty()) {
                return;
            }
            switch (shortMessage.getCommand()) {
                case ShortMessage.NOTE_ON -> {
                    if (this.instrument.singleTone() && lastNote != -1) {
                        innerReceiver.send(MidiHelper.stopMessage(lastNote + instrument.initialPitch()), -1);
                    }
                    innerReceiver.send(MidiHelper.startMessage(shortMessage.getData1() + instrument.initialPitch(),
                            (int) (shortMessage.getData2() * instrument.initialVolume())), -1);
                    lastNote = shortMessage.getData1();
                }
                case ShortMessage.NOTE_OFF -> {
                    innerReceiver.send(MidiHelper.stopMessage(shortMessage.getData1() + instrument.initialPitch()), -1);
                    if (shortMessage.getData1() == lastNote) {
                        lastNote = -1;
                    }
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
