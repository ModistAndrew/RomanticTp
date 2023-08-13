package modist.romantictp.client.sound.util;

import modist.romantictp.util.TooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

public record MidiInfo(byte[] data, long time, String author) implements TooltipProvider {
    public static final MidiInfo EMPTY = new MidiInfo(new byte[0], 0, "");
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("data", data);
        tag.putLong("time", time);
        tag.putString("author", author);
        return tag;
    }

    public MidiInfo(CompoundTag nbt) {
        this(nbt.getByteArray("data"), nbt.getLong("time"), nbt.getString("author"));
    }

    public boolean isEmpty(){
        return data.length == 0;
    }

    public String getTimeString() {
        long second = time % 60;
        long minute = time / 60;
        return minute + ":" + (second < 10 ? "0" + second : second);
    }

    @Override
    public void addText(List<Component> pTooltip, boolean advanced) {
        if(!this.isEmpty()) {
            TooltipProvider.addTooltip("time", getTimeString(), pTooltip, ChatFormatting.AQUA);
            TooltipProvider.addTooltip("author", author, pTooltip, ChatFormatting.AQUA);
        } else {
            TooltipProvider.addTooltip("empty", pTooltip, ChatFormatting.RED);
        }
    }
}
