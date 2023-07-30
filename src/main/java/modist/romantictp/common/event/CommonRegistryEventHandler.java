package modist.romantictp.common.event;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.entity.EntityLoader;
import modist.romantictp.common.entity.Melody;
import modist.romantictp.common.instrument.ScoreTicker;
import modist.romantictp.network.NetworkHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistryEventHandler {


    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.registerMessage();
    }

    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(EntityLoader.MELODY.get(), Melody.createAttributes().build());
    }
}
