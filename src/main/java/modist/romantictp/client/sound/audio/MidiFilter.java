package modist.romantictp.client.sound.audio;

import modist.romantictp.client.sound.MyChannel;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.sounds.ChannelAccess;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//Thread safety: handling audio is OK. No direct access to Render Thread.
//filter message (by instrument) to synthesizer receiver. You can send message from manager -> instance -> here or attach a transmitter
//channel will always be 1 as there is only one instrument playing at a time
public class MidiFilter implements Receiver {
    private final CompletableFuture<ChannelAccess.ChannelHandle> channelHandle = new CompletableFuture<>();
    private volatile Instrument instrument = Instrument.EMPTY;
    private int lastNote = -1;

    public void setChannel(ChannelAccess.ChannelHandle channelHandle) {
        this.channelHandle.complete(channelHandle);
    }

    public void setInstrument(Instrument instrument) {
        if(this.instrument.equals(instrument)){
            return;
        }
        this.instrument = instrument;
        updateInstrument();
    }

    public int getLastNote(){
        return lastNote;
    }

    private void updateInstrument() {
        executeOnReceiver(receiver -> {
            for (int i = 0; i < 128; i++) {
                receiver.send(MidiHelper.stopMessage(i), -1);
            }
            lastNote = -1;
            receiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 123, 0), -1);
            receiver.send(MidiHelper.message(ShortMessage.CONTROL_CHANGE, 64, 0), -1);
            if (!this.instrument.isEmpty()) {
                receiver.send(MidiHelper.instrumentMessage(this.instrument.instrumentId()), -1);
            }
        });
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage shortMessage) {
            if(this.instrument.isEmpty()){
                return;
            }
            executeOnReceiver(receiver -> {
                switch (shortMessage.getCommand()) {
                    case ShortMessage.NOTE_ON -> {
                        if (this.instrument.singleTone() && lastNote != -1) {
                            receiver.send(MidiHelper.stopMessage(lastNote + instrument.initialPitch()), -1);
                        }
                        receiver.send(MidiHelper.startMessage(shortMessage.getData1() + instrument.initialPitch(),
                                (int) (shortMessage.getData2() * instrument.initialVolume())), -1);
                        lastNote = shortMessage.getData1();
                    }
                    case ShortMessage.NOTE_OFF -> {
                        receiver.send(MidiHelper.stopMessage(shortMessage.getData1() + instrument.initialPitch()), -1);
                        if (shortMessage.getData1() == lastNote) {
                            lastNote = -1;
                        }
                    }
                    case ShortMessage.PROGRAM_CHANGE -> {
                        //skip instrument change
                    }
                    case ShortMessage.CONTROL_CHANGE -> {
                        if(shortMessage.getData1() != 0) { //skip instrument bank change
                            receiver.send
                                    (MidiHelper.message(shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2()), -1);
                        }
                    }
                    default -> receiver.send
                            (MidiHelper.message(shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2()), -1);
                }
            });
        } else {
            executeOnReceiver(receiver -> receiver.send(message, timeStamp));
        }
    }

    private void executeOnReceiver(Consumer<Receiver> innerReceiver) {
        channelHandle.thenAcceptAsync(c -> c.execute
                (channel -> innerReceiver.accept(((MyChannel) channel).receiver)));
    }

    @Override
    public void close() {
    }
}
