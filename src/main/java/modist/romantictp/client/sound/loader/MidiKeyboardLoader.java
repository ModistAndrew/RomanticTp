package modist.romantictp.client.sound.loader;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.config.RomanticTpConfig;
import modist.romantictp.client.sound.midi.LocalReceiver;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MidiKeyboardLoader implements ResourceManagerReloadListener {
    private static final MidiKeyboardLoader instance = new MidiKeyboardLoader();
    @Nullable
    private MidiDevice midiKeyboard;

    public static MidiKeyboardLoader getInstance() {
        return instance;
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        getAvailableDevices().forEach(midiDevice ->
                RomanticTp.LOGGER.info("Available midi input device founded: {}", midiDevice.getDeviceInfo().getName()));
        List<MidiDevice> device = getAvailableDevices().stream()
                .filter(midiDevice -> midiDevice.getDeviceInfo().getName().equals(RomanticTpConfig.MIDI_KEYBOARD.get())).toList();
        if (!device.isEmpty()) {
            midiKeyboard = device.get(0);
            try {
                midiKeyboard.getTransmitter().setReceiver(LocalReceiver.getInstance());
                midiKeyboard.open();
                RomanticTp.LOGGER.info("Midi keyboard loaded: {}", midiKeyboard.getDeviceInfo().getName());
            } catch (MidiUnavailableException e) {
                RomanticTp.LOGGER.error("Midi keyboard error: ", e);
            }
        } else if (!RomanticTpConfig.MIDI_KEYBOARD.get().isEmpty()) {
            RomanticTp.LOGGER.warn("Midi keyboard not found: {}", RomanticTpConfig.MIDI_KEYBOARD.get());
        }
    }

    private List<MidiDevice> getAvailableDevices() {
        List<MidiDevice> devices = new ArrayList<>();

        // Devices
        for (int i = 0; i < MidiSystem.getMidiDeviceInfo().length; i++) {
            try {
                devices.add(MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]));
            } catch (MidiUnavailableException e) {
                RomanticTp.LOGGER.error("Midi device error. Device will be skipped.", e);
            }
        }

        devices = devices.stream()
                .filter(d -> d.getMaxTransmitters() != 0)
                .filter(d -> !d.getClass().getName().contains("com.sun.media.sound.RealTimeSequencer"))
                .collect(Collectors.toList());
        return devices;
    }
}
