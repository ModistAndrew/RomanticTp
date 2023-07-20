package modist.romantictp.client.sound;

import modist.romantictp.client.audio.MyDataLine;
import modist.romantictp.client.audio.MyInputStream;
import modist.romantictp.client.audio.fork.sound.AudioSynthesizer;
import modist.romantictp.client.audio.fork.sound.SF2Soundbank;
import modist.romantictp.client.audio.fork.sound.SoftSynthesizer;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import javax.sound.midi.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.PipedInputStream;
import java.util.List;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    private final Instrument instrument;
    private LivingEntity player;
    private static final int MAX_LEFT_TICK = 10;
    private int leftTick = MAX_LEFT_TICK;
    public ChannelAccess.ChannelHandle channelHandle;

    public InstrumentSoundInstance(LivingEntity player, Instrument instrument) {
        super(instrument.sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.instrument = instrument;
        this.player = player;
    }

    public void bindChannel(ChannelAccess.ChannelHandle channelHandle){
        this.channelHandle = channelHandle;
    }
    @Override
    public void tick() {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}