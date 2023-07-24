package modist.romantictp.client.sound.audio;

import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.sounds.ChannelAccess;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//Thread safety: handling audio is OK. No direct access to Render Thread.
public class OuterReceiver implements Receiver { //filter message (by instrument) to synthesizer receiver
    private final CompletableFuture<ChannelAccess.ChannelHandle> channelHandle = new CompletableFuture<>();
    @Nullable
    private volatile Instrument instrument;

    public OuterReceiver() {
    }

    public void setChannel(ChannelAccess.ChannelHandle channelHandle) {
        this.channelHandle.complete(channelHandle);
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) { //TODO channel,... system
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
