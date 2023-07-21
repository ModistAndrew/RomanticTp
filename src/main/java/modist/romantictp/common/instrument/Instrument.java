package modist.romantictp.common.instrument;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class Instrument {
    public final UUID id;
    public final float initialPitch;
    public final float initialVolume;
    public final boolean singleTone;
    public int instrumentId;
    protected static final Random RANDOM = new Random();
    public static final String PREFIX = "instrument_properties.romantictp.";

    public Instrument(float initialPitch, float initialVolume, int id, boolean singleTone) {
        this.id = UUID.randomUUID();
        this.initialPitch = initialPitch;
        this.initialVolume = initialVolume;
        this.instrumentId = id;
        this.singleTone = singleTone;
    }

    public Instrument(CompoundTag tag) {
        this.id = NbtUtils.loadUUID(tag.get("id"));
        this.initialPitch = tag.getFloat("initialPitch");
        this.initialVolume = tag.getFloat("initialVolume");
        this.instrumentId = tag.getInt("instrumentId");
        this.singleTone = tag.getBoolean("singleTone");
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("id", NbtUtils.createUUID(id));
        tag.putFloat("initialPitch", initialPitch);
        tag.putFloat("initialVolume", initialVolume);
        tag.putInt("instrumentId", instrumentId);
        tag.putBoolean("singleTone", singleTone);
        return tag;
    }

    public void addText(List<Component> pTooltip, boolean showItems) {
        addTooltip("id", id, pTooltip, ChatFormatting.AQUA);
        addTooltip("initialPitch", initialPitch, pTooltip, ChatFormatting.AQUA);
        addTooltip("initialVolume", initialVolume, pTooltip, ChatFormatting.AQUA);
    }

    public static void addTooltip(String name, Object value, List<Component> pTooltip, ChatFormatting... pFormats) {
        MutableComponent mutablecomponent = Component.translatable(PREFIX+name);
        mutablecomponent.append(": ").append(String.valueOf(value));
        pTooltip.add(mutablecomponent.withStyle(pFormats));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Instrument instrument){
            return this.id.equals(instrument.id);
        }
        return false;
    }
}
