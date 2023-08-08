package modist.romantictp.common.instrument;

import modist.romantictp.client.sound.efx.ReverbType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public record Instrument(int initialPitch, float initialVolume, int instrumentId, boolean singleTone, ReverbType reverb) {
    //initialPitch: added to pitch in midi
    //initialVolume: multiplied to velocity in midi
    public static final String PREFIX = "instrument_properties.romantictp.";
    public static final Instrument EMPTY = new Instrument(0, 0, -1, false, ReverbType.EMPTY);

    public Instrument(CompoundTag tag) {
        this(tag.getInt("initialPitch"), tag.getFloat("initialVolume"), tag.getInt("instrumentId"), tag.getBoolean("singleTone"),
                ReverbType.fromString(tag.getString("reverb")));
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("initialPitch", initialPitch);
        tag.putFloat("initialVolume", initialVolume);
        tag.putInt("instrumentId", instrumentId);
        tag.putBoolean("singleTone", singleTone);
        tag.putString("reverb", reverb.name());
        return tag;
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public void addText(List<Component> pTooltip, boolean showItems) {
        addTooltip("initialPitch", initialPitch, pTooltip, ChatFormatting.AQUA);
        addTooltip("initialVolume", initialVolume, pTooltip, ChatFormatting.AQUA);
        addTooltip("singleTone", singleTone, pTooltip, ChatFormatting.AQUA);
        addTooltip("reverb", reverb, pTooltip, ChatFormatting.AQUA);
    }

    public static void addTooltip(String name, Object value, List<Component> pTooltip, ChatFormatting... pFormats) {
        MutableComponent mutablecomponent = Component.translatable(PREFIX + name);
        mutablecomponent.append(": ").append(String.valueOf(value));
        pTooltip.add(mutablecomponent.withStyle(pFormats));
    }

    public static class Builder {
        private final int instrumentId;
        private int initialPitch = 0;
        private float initialVolume = 2F;
        private boolean singleTone = true;
        private ReverbType reverb = ReverbType.EMPTY;

        private Builder(int instrumentId){
            this.instrumentId = instrumentId;
        }

        public static Builder of(int instrumentId) {
            return new Builder(instrumentId);
        }

        public Builder initialPitch(int initialPitch) {
            this.initialPitch = initialPitch;
            return this;
        }

        public Builder initialVolume(float initialVolume) {
            this.initialVolume = initialVolume;
            return this;
        }

        public Builder singleTone(boolean singleTone) {
            this.singleTone = singleTone;
            return this;
        }

        public Builder reverb(ReverbType reverb) {
            this.reverb = reverb;
            return this;
        }

        public Instrument build() {
            return new Instrument(initialPitch, initialVolume, instrumentId, singleTone, reverb);
        }
    }
}
