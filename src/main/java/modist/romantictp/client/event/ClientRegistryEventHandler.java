package modist.romantictp.client.event;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.block.entity.AutoPlayerRenderer;
import modist.romantictp.client.block.entity.InstrumentRenderer;
import modist.romantictp.client.entity.MelodyModel;
import modist.romantictp.client.entity.MelodyRenderer;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.loader.MidiKeyboardLoader;
import modist.romantictp.client.sound.loader.SynthesizerPool;
import modist.romantictp.common.block.BlockLoader;
import modist.romantictp.common.entity.EntityLoader;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegistryEventHandler {
    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        InstrumentKeyMapping.PITCHES.forEach(l -> event.register(l.get()));
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(BlockLoader.INSTRUMENT_BLOCK_ENTITY.get(), c -> new InstrumentRenderer(c.getItemRenderer()));
        BlockEntityRenderers.register(BlockLoader.AUTO_PLAYER_BLOCK_ENTITY.get(), c -> new AutoPlayerRenderer(c.getBlockRenderDispatcher()));
    }

    @SubscribeEvent
    public static void loadResource(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(MidiFileLoader.getInstance());
        event.registerReloadListener(SynthesizerPool.getInstance());
        event.registerReloadListener(MidiKeyboardLoader.getInstance());
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MelodyModel.MELODY, MelodyModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityLoader.MELODY.get(), MelodyRenderer::new);
    }

    @SubscribeEvent
    public static void addSpecialModels(ModelEvent.RegisterAdditional event) {
        event.register(AutoPlayerRenderer.DISC_LOCATION);
    }
}
