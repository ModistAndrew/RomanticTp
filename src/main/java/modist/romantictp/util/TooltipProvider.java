package modist.romantictp.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public interface TooltipProvider {
    String PREFIX = "properties.romantictp.";

    void addText(List<Component> pTooltip, boolean advanced);

    static void addTooltip(String name, Object value, List<Component> pTooltip, ChatFormatting... pFormats) {
        MutableComponent mutablecomponent = Component.translatable(PREFIX + name);
        mutablecomponent.append(": ").append(String.valueOf(value));
        pTooltip.add(mutablecomponent.withStyle(pFormats));
    }

    static void addTooltip(String s, List<Component> pTooltip, ChatFormatting... pFormats) {
        pTooltip.add(Component.literal(s).withStyle(pFormats));
    }
}
