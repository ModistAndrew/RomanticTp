package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.AlDataLine;
import modist.romantictp.client.sound.fork.gervill.SoftSynthesizer;
import modist.romantictp.client.sound.util.AudioHelper;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public record SynthesizerWrapper(SoftSynthesizer synthesizer, Receiver receiver, AlDataLine dataLine) {
    @Nullable
    public static SynthesizerWrapper create() {
        try {
            SoftSynthesizer synthesizer = new SoftSynthesizer();
            AudioFormat audioFormat = AudioHelper.AUDIO_FORMAT;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            AlDataLine alDataLine = new AlDataLine(sourceDataLine);
            Map<String, Object> params = new HashMap<>();
            params.put("jitter correction", RomanticTpConfig.JITTER_CORRECTION.get());
            synthesizer.open(alDataLine, params);
            alDataLine.muteInnerDataLine();
            if (SoundbankLoader.getInstance().soundbank != null) {
                synthesizer.loadAllInstruments(SoundbankLoader.getInstance().soundbank);
            }
            return new SynthesizerWrapper(synthesizer, synthesizer.getReceiver(), alDataLine);
        } catch (MidiUnavailableException | LineUnavailableException e) {
            RomanticTp.LOGGER.warn("Fail to init synthesizer", e);
            return null;
        }
    }

    public void delete() {
        this.synthesizer.close();
    }

    public void free() {
        dataLine.bindChannel(null);
    }
}
