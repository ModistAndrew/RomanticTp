package modist.romantictp.client.instrument;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.world.phys.Vec3;

public interface InstrumentPlayer {
    Vec3 getPos();
    float getVolume();
    Instrument getInstrument(); //get current instrument holding. set to EMPTY to stop all sounds.
    boolean isPlaying(); //for sequence check. should be synchronized from server. sequencer stopping should be managed by tick.
    boolean isRemoved(); //remove
    void addParticle(int note); //TODO: distribute
}
