package modist.romantictp.client.sound;

import com.mojang.logging.LogUtils;
import modist.romantictp.client.audio.MyChannel;
import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.slf4j.Logger;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class InstrumentSoundManager {
    public static final Logger LOGGER = LogUtils.getLogger();
    private EFXManager efx;
    static InstrumentSoundManager instance = new InstrumentSoundManager();
    //now playing, use instrument UUID to map
    private Map<UUID, InstrumentSoundInstance> soundInstanceCache = new HashMap<>();
    //TODO quit Game? ESC?
    public void init(){
        efx = new EFXManager();
    }

    public static InstrumentSoundManager getInstance() {
        return instance;
    }

    public void applyEFX(int source) {
        efx.applyEFX(source);
    }

    public void sendMessage
            (LivingEntity player, Instrument instrument, MidiMessage message, long timeStamp) {
        InstrumentSoundInstance soundInstance = getSound(instrument);
        if(soundInstance==null){
            soundInstance = new InstrumentSoundInstance(player, instrument);
            Minecraft.getInstance().getSoundManager().play(soundInstance);
            soundInstanceCache.put(instrument.id, soundInstance);
        }
        if(soundInstance.channelHandle!=null) {
            soundInstance.channelHandle.execute(channel -> {
                if (channel instanceof MyChannel myChannel) {
                    myChannel.receiver.send(message, timeStamp);
                }
            });
        }
    }

    private InstrumentSoundInstance getSound(Instrument instrument){
        return instrument==null ? null : soundInstanceCache.get(instrument.id);
    }

    public void startPlay(LivingEntity player, Instrument instrument, float pitch, float volume) {
        sendMessage(player, instrument, makeEvent(144, 1, (int) (60 * pitch), 100, 0).getMessage(), -1);
    }

    public void stopPlay(LivingEntity player, Instrument instrument) {
        sendMessage(player, instrument, makeEvent(128, 1, 60, 100, 2).getMessage(), -1);
    }

    public static MidiEvent makeEvent(int command, int channel,
                                      int note, int velocity, int tick)
    {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);
            event = new MidiEvent(a, tick);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return event;
    }

    private class EFXManager {
        private int auxFXSlot;
        private int reverb;
        private int directFilter;
        private int sendFilter;
        private int maxAuxSends;

        private EFXManager(){
            //Get current context and device
            long currentContext = ALC10.alcGetCurrentContext();
            long currentDevice = ALC10.alcGetContextsDevice(currentContext);
            if (ALC10.alcIsExtensionPresent(currentDevice, "ALC_EXT_EFX")) {
                LOGGER.info("EFX Extension recognized");
            } else {
                LOGGER.error("EFX Extension not found on current device. Aborting.");
                return;
            }

            maxAuxSends = ALC10.alcGetInteger(currentDevice, EXTEfx.ALC_MAX_AUXILIARY_SENDS);
            LOGGER.info("Max auxiliary sends: {}", maxAuxSends);

            // Create auxiliary effect slots
            auxFXSlot = EXTEfx.alGenAuxiliaryEffectSlots();
            LOGGER.info("Aux slot {} created", auxFXSlot);
            EXTEfx.alAuxiliaryEffectSloti(auxFXSlot, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL11.AL_TRUE);

            reverb = EXTEfx.alGenEffects();
            EXTEfx.alEffecti(reverb, EXTEfx.AL_EFFECT_TYPE, EXTEfx.AL_EFFECT_EAXREVERB);

            directFilter = EXTEfx.alGenFilters();
            EXTEfx.alFilteri(directFilter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);

            sendFilter = EXTEfx.alGenFilters();
            EXTEfx.alFilteri(sendFilter, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);

            setReverbParams(auxFXSlot, reverb);
        }

        protected static void setReverbParams(int auxFXSlot, int reverbSlot) {
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DENSITY, 1F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DIFFUSION, 1F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAIN, 0.4F * 0.7F * 0.85F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_GAINHF, 0.89F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_TIME, 4.142F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_DECAY_HFRATIO, 0.7F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN, 2.5F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN, 1.26F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY, 0.021F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF, 0.994F);
            EXTEfx.alEffectf(reverbSlot, EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR, 0.11F);
            // Attach updated effect object
            EXTEfx.alAuxiliaryEffectSloti(auxFXSlot, EXTEfx.AL_EFFECTSLOT_EFFECT, reverbSlot);
        }

        public void applyEFX(int source) {
            EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAIN, 1F);
            EXTEfx.alFilterf(sendFilter, EXTEfx.AL_LOWPASS_GAINHF, 1F);
            AL11.alSource3i(source, EXTEfx.AL_AUXILIARY_SEND_FILTER, auxFXSlot, 0, EXTEfx.AL_FILTER_NULL);
            //AL10.alSourcef(source, AL10.AL_PITCH, 2.0F);
        }
    }
}
