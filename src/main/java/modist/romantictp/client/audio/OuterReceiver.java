package modist.romantictp.client.audio;

import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.client.sound.InstrumentSoundManager;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.sounds.ChannelAccess;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OuterReceiver implements Receiver { //filter message (by instrument) to synthesizer receiver
    private final InstrumentPlayer player;
    private CompletableFuture<ChannelAccess.ChannelHandle> channelHandle;

    public OuterReceiver(InstrumentPlayer player) {
        this.player = player;
    }

    public void setChannel(CompletableFuture<ChannelAccess.ChannelHandle> channelHandle) {
        this.channelHandle = channelHandle;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) { //TODO channel,... system
        Instrument instrument = player.getInstrument();
        if(instrument == null){
            return;
        }
        sendMessage(receiver -> {
            try {
                receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, 1, instrument.instrumentId, 0), -1);
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException(e);
            }
            receiver.send(message, timeStamp);
        });
    }

    private void sendMessage(Consumer<Receiver> innerReceiver){
        channelHandle.thenAcceptAsync(c -> c.execute
                (channel -> innerReceiver.accept(((MyChannel) channel).receiver)));
    }

    @Override
    public void close() {
    }
}
