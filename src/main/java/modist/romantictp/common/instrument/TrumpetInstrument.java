package modist.romantictp.common.instrument;

import net.minecraft.nbt.CompoundTag;

public class TrumpetInstrument extends Instrument {
    public TrumpetInstrument(float initialPitch, float initialVolume){
        super(initialPitch, initialVolume, "trumpet", true);
    }

    public TrumpetInstrument(){
        this(RANDOM.nextFloat(), RANDOM.nextFloat());
    }

    public TrumpetInstrument(CompoundTag tag){
        super(tag);
    }
}
