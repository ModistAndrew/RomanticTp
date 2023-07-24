package modist.romantictp.client.sound.audio;

import modist.romantictp.client.sound.audio.MyChannel;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.client.instrument.InstrumentPlayer;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.sounds.ChannelAccess;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
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
            receiver.send(MidiHelper.instrumentMessage(1, instrument.instrumentId), -1);
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