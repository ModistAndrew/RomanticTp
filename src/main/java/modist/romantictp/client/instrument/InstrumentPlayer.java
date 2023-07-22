package modist.romantictp.client.instrument;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface InstrumentPlayer {
    public Vec3 getPos();
    public float geVolume();

    @Nullable
    public Instrument getInstrument();
    @Nullable
    public Instrument getActiveInstrument();
}
