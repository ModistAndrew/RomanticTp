package modist.romantictp.common.instrument;

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
