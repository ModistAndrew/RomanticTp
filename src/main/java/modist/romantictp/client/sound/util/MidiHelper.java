package modist.romantictp.client.sound.util;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.util.StringHelper;
import modist.romantictp.util.TooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class MidiHelper {
    public static ShortMessage message(int command, int note, int velocity) {
        ShortMessage message;
        try {
            message = new ShortMessage(command, 1, note, velocity);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    public static ShortMessage startMessage(int note, int velocity){
        return message(ShortMessage.NOTE_ON, clip(note), clip(velocity));
    }

    public static ShortMessage stopMessage(int note){
        return message(ShortMessage.NOTE_OFF, clip(note), 0);
    }

    public static ShortMessage instrumentMessage(int instrument){
        return message(ShortMessage.PROGRAM_CHANGE, instrument, 0);
    }

    private static int clip(int value) {
        return Math.max(0, Math.min(127, value));
    }

    public static void save(FriendlyByteBuf buf, ShortMessage message){
        buf.writeInt(message.getCommand());
        buf.writeInt(message.getData1());
        buf.writeInt(message.getData2());
    }

    public static ShortMessage load(FriendlyByteBuf buf){
        int command = buf.readInt();
        int data1 = buf.readInt();
        int data2 = buf.readInt();
        return message(command, data1, data2);
    }

    @Nullable
    public static Sequence loadSequence(byte[] data){
        try {
            return data.length>0 ? MidiSystem.getSequence(new ByteArrayInputStream(data)) : null;
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MidiInfo getInfo(String path) {
        try {
            byte[] data = MidiFileLoader.getInstance().getMidiData(path);
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

    public record MidiInfo(byte[] data, long time, String group, String name, String author, String section) implements TooltipProvider {
        public static final MidiInfo EMPTY = new MidiInfo(new byte[0], 0, "", "", "", "");
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
                TooltipProvider.addTooltip(name, pTooltip, ChatFormatting.WHITE);
                TooltipProvider.addTooltip(StringHelper.TIME, getTimeString(), pTooltip, ChatFormatting.AQUA);
                if(!author.isEmpty()) {
                    TooltipProvider.addTooltip(StringHelper.AUTHOR, author, pTooltip, ChatFormatting.GREEN);
                }
                if(!group.isEmpty()) {
                    TooltipProvider.addTooltip(StringHelper.GROUP, group, pTooltip, ChatFormatting.YELLOW);
                }
                if(!section.isEmpty()) {
                    TooltipProvider.addTooltip(StringHelper.SECTION, section, pTooltip, ChatFormatting.GOLD);
                }
            } else {
                TooltipProvider.addTooltip("EMPTY", pTooltip, ChatFormatting.RED);
            }
        }
    }
}
