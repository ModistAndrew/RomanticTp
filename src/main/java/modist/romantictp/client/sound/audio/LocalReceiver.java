package modist.romantictp.client.sound.audio;

import modist.romantictp.client.instrument.InstrumentPlayerManager;
import modist.romantictp.client.sound.InstrumentSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class LocalReceiver implements Receiver {
    private static final LocalReceiver instance = new LocalReceiver();
    public static LocalReceiver getInstance(){
        return instance;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if(message instanceof ShortMessage shortMessage) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !Minecraft.getInstance().isPaused()) {
                InstrumentSoundManager.getInstance().sendMessage
                        (InstrumentPlayerManager.getOrCreate(player), shortMessage, timeStamp, true);
            }
        }
    }

    @Override
    public void close() {

    }
}
