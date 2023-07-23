package modist.romantictp.client.instrument;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface InstrumentPlayer {
    Vec3 getPos();
    float getVolume();
    @Nullable
    Instrument getInstrument(); //get current instrument holding
    boolean isPlaying();
    String getScore();
}
