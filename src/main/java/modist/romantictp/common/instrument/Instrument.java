package modist.romantictp.common.instrument;

import modist.romantictp.client.sound.efx.ReverbType;
import modist.romantictp.client.sound.util.StringHelper;
import modist.romantictp.util.TooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

public record Instrument(int instrumentId, ReverbType reverb)
        implements TooltipProvider {
    public static final Instrument EMPTY = new Instrument(-1, ReverbType.GENERIC);

    public Instrument(CompoundTag tag) {
        this(tag.getInt("instrumentId"), ReverbType.fromString(tag.getString("reverb")));
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("instrumentId", instrumentId);
        tag.putString("reverb", reverb.name());
        return tag;
    }

    public boolean isEmpty() {
        return instrumentId < 0;
    }

    @Override
    public void addText(List<Component> pTooltip, boolean showItems) {
        if(!isEmpty()) {
            TooltipProvider.addTooltip(StringHelper.ID, instrumentId, pTooltip, ChatFormatting.AQUA);
            if (isSpecial()) {
                TooltipProvider.addTooltip(StringHelper.REVERB, StringHelper.title(reverb.name()), pTooltip, ChatFormatting.GOLD);
            }
        } else {
            TooltipProvider.addTooltip("EMPTY", pTooltip, ChatFormatting.RED);
        }
    }

    public boolean isSpecial() {
        return reverb != ReverbType.GENERIC;
    }

    public static class Builder {
        private final int instrumentId;
        private ReverbType reverb = ReverbType.GENERIC;

        private Builder(int instrumentId){
            this.instrumentId = instrumentId;
        }

        public static Builder of(int instrumentId) {
            return new Builder(instrumentId);
        }

        public Builder reverb(ReverbType reverb) {
            this.reverb = reverb;
            return this;
        }

        public Instrument build() {
            return new Instrument(instrumentId, reverb);
        }
    }
}
