package modist.romantictp.client.sound.util;

import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.util.TooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import javax.sound.midi.*;
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

    public static MidiInfo getInfo(String name) {
        try {
            byte[] data = MidiFileLoader.getInstance().getMidiData(name);
            long time = data.length>0 ?
                    MidiSystem.getSequence(new ByteArrayInputStream(data)).getMicrosecondLength() / 1000000 + 1 : 0;
            String author = name;
            return new MidiInfo(data, time, author);
        } catch (InvalidMidiDataException | IOException e) {
            throw new RuntimeException(e);
        }
    }

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
}
