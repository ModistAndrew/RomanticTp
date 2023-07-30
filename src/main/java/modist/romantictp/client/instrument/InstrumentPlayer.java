package modist.romantictp.client.instrument;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface InstrumentPlayer {
    Vec3 getPos();
    float getVolume();
    Instrument getInstrument(); //get current instrument holding. set to EMPTY to stop all sounds.
    boolean isPlaying(); //for sequence check
    void updateSequenceStatus(float progress);
    void stopPlaying(); //reset from sequencer. you may need to synchronize to server.
    CompoundTag serializeNBT(); //client
    boolean isRemoved(); //remove
}
