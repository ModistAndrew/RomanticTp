package modist.romantictp.client.sound.util;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.util.StringHelper;
import modist.romantictp.util.TooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public record MidiInfo(byte[] data, long time, String group, String name, String author, String section)
        implements TooltipProvider, Comparable<MidiInfo> {
    public static final MidiInfo EMPTY = new MidiInfo(new byte[0], 0, "", "", "", "");

    public static MidiInfo create(String path, byte[] data) {
        try {
            if(data.length== 0){
                return MidiInfo.EMPTY;
            }
            long time = MidiSystem.getSequence(new ByteArrayInputStream(data)).getMicrosecondLength() / 1000000 + 1;
            String[] strings = StringHelper.splitMidiName(path);
            String group = StringHelper.getGroup(strings[0]);
            String displayName = StringHelper.title(StringHelper.getDisplayName(strings[0]));
            String author = "";
            String section = "";
            if(strings.length > 1) {
                author = StringHelper.title(strings[1]);
                if (strings.length > 2) {
                    section = StringHelper.title(strings[2]);
                }
            }
            return new MidiInfo(data, time, group, displayName, author, section);
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("data", data);
        tag.putLong("time", time);
        tag.putString("group", group);
        tag.putString("name", name);
        tag.putString("author", author);
        tag.putString("section", section);
        return tag;
    }

    public MidiInfo(CompoundTag nbt) {
        this(nbt.getByteArray("data"), nbt.getLong("time"), nbt.getString("group"),
                nbt.getString("name"), nbt.getString("author"), nbt.getString("section"));
    }

    public boolean isEmpty() {
        return data.length == 0;
    }

    public String getTimeString() {
        long second = time % 60;
        long minute = time / 60;
        return minute + ":" + (second < 10 ? "0" + second : second);
    }

    @Override
    public void addText(List<Component> pTooltip, boolean advanced) {
        if (!this.isEmpty()) {
            TooltipProvider.addTooltip(name, pTooltip, ChatFormatting.WHITE, ChatFormatting.ITALIC);
            TooltipProvider.addTooltip(StringHelper.TIME, getTimeString(), pTooltip, ChatFormatting.AQUA);
            if (!author.isEmpty()) {
                TooltipProvider.addTooltip(StringHelper.AUTHOR, author, pTooltip, ChatFormatting.GREEN);
            }
            if (!group.isEmpty()) {
                TooltipProvider.addTooltip(StringHelper.GROUP, group, pTooltip, ChatFormatting.YELLOW);
            }
            if (!section.isEmpty()) {
                TooltipProvider.addTooltip(StringHelper.SECTION, section, pTooltip, ChatFormatting.GOLD);
            }
        } else {
            TooltipProvider.addTooltip("EMPTY", pTooltip, ChatFormatting.RED);
        }
    }

    @Override
    public int compareTo(MidiInfo other) {
        return Comparator.comparing((MidiInfo i) -> i.group)
                .thenComparing(MidiInfo::author)
                .thenComparing(MidiInfo::name)
                .thenComparing(MidiInfo::section)
                .compare(this, other);
    }
}
