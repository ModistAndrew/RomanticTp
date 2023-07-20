package modist.romantictp.client.audio;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.OpenAlUtil;
import com.mojang.blaze3d.audio.SoundBuffer;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.audio.fork.sound.AudioSynthesizer;
import modist.romantictp.client.audio.fork.sound.SoftSynthesizer;
import net.minecraft.client.sounds.AudioStream;
import org.lwjgl.openal.AL10;

import javax.sound.midi.Receiver;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.PipedInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

public class MyChannel extends Channel {
    public AudioSynthesizer synthesizer;
    public Receiver receiver;
    private final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
    public volatile boolean pumping;
    public final AtomicInteger pumpCount = new AtomicInteger();

    public MyChannel(int source) {
        super(source);
        initAudio();
    }

    public static MyChannel create() {
        int[] aint = new int[1];
        AL10.alGenSources(aint);
        return new MyChannel(aint[0]);
    }

    private void initAudio() {
        try {
            this.synthesizer = new SoftSynthesizer();

            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info1);
            PipedInputStream pipedInputStream = new PipedInputStream();
            this.synthesizer.open(new MyDataLine(this, sourceDataLine, pipedInputStream), null);

//            SF2Soundbank soundbank = new SF2Soundbank(new File("C:\\Users\\zjx\\Desktop\\Music\\Touhou1.sf2"));
//            this.synthesizer.loadAllInstruments(soundbank);

            this.receiver = synthesizer.getReceiver();

            this.stream = new MyInputStream(pipedInputStream);

//            MidiDevice device;
//            MidiDevice.Info[] infoList = MidiSystem.getMidiDeviceInfo();
//            for (MidiDevice.Info info : infoList) {
//                try {
//                    device = MidiSystem.getMidiDevice(info);
//                    System.out.println(info);
//
//                    Transmitter trans = device.getTransmitter();
//                    trans.setReceiver(receiver);
//
//                    device.open();
//                    System.out.println(device.getDeviceInfo() + " Was Opened");
//
//                } catch (MidiUnavailableException ignored) {
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attachBufferStream(AudioStream pStream) {
        this.streamingBufferSize = 2048;
        this.pumpBuffers(8);
    }

    @Override
    public boolean stopped() {
        return false;
    }

    @Override
    public void destroy() {
        this.synthesizer.close();
        super.destroy();
    }

    @Override
    public void updateStream() {
        if (AL10.alGetSourcei(this.source, 4112) == AL10.AL_STOPPED) {
            AL10.alSourcePlay(this.source);
            this.pumpBuffers(4);
            RomanticTp.LOGGER.info("YES!!!");
        }
        super.updateStream();
    }

    @Override
    public void pumpBuffers(int pReadCount) {
        pumpCount.set(pReadCount);
        synchronized (pumpCount){
            if(pumpCount.get() > 0){
                pumpCount.notify();
            }
        }
        RomanticTp.LOGGER.info("Starting pumping" + pReadCount);
    }

    public void write(byte[] b, int off, int len) {
        synchronized (pumpCount){
            if(pumpCount.get() <= 0){
                try {
                    pumpCount.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        pumpCount.decrementAndGet();
        RomanticTp.LOGGER.info("Receiving" + len);
        ByteBuffer bytebuffer = AudioLoader.convertAudioBytes(b, audioFormat.getSampleSizeInBits() == 16,
                audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        if (bytebuffer != null) {
            (new SoundBuffer(bytebuffer, this.stream.getFormat())).releaseAlBuffer().ifPresent((p_83669_) -> {
                AL10.alSourceQueueBuffers(this.source, new int[]{p_83669_});
            });
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void unpause() {
    }
}
