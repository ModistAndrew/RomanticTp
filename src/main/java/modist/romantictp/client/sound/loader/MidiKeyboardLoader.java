package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.audio.LocalReceiver;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MidiKeyboardLoader {
    private static final MidiKeyboardLoader instance = new MidiKeyboardLoader();
    @Nullable
    private MidiDevice midiKeyboard;

    public static MidiKeyboardLoader getInstance() {
        return instance;
    }

    public void init() {
        RomanticTp.info("Available midi input device:");
        getAvailableDevices().forEach(midiDevice -> RomanticTp.info(midiDevice.getDeviceInfo().getName()));
        List<MidiDevice> device = getAvailableDevices().stream()
                .filter(midiDevice -> midiDevice.getDeviceInfo().getName().equals(RomanticTpConfig.MIDI_KEYBOARD.get())).toList();
        if(!device.isEmpty()){
            midiKeyboard = device.get(0);
            try {
                midiKeyboard.getTransmitter().setReceiver(LocalReceiver.getInstance());
                midiKeyboard.open();
                RomanticTp.info("Midi keyboard loaded: " + midiKeyboard.getDeviceInfo().getName());
            } catch (MidiUnavailableException e) {
                RomanticTp.LOGGER.warn("Midi keyboard error: ", e);
            }
        }
    }

    private List<MidiDevice> getAvailableDevices() {
        List<MidiDevice> devices = new ArrayList<>();

        // Devices
        for (int i = 0; i < MidiSystem.getMidiDeviceInfo().length; i++) {
            try {
                devices.add(MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]));
            } catch (MidiUnavailableException e) {
                RomanticTp.LOGGER.warn("Midi device error. Device will be skipped. Error: ", e);
            }
        }

        devices = devices.stream()
                .filter(d -> d.getMaxTransmitters() != 0)
                .filter(d -> !d.getClass().getName().contains("com.sun.media.sound.RealTimeSequencer"))
                .collect(Collectors.toList());
        return devices;
    }
}
