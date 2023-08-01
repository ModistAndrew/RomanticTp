package modist.romantictp.common.event;

import com.mojang.datafixers.util.Pair;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.loader.MidiFileLoader;
import modist.romantictp.client.sound.util.MidiHelper;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.entity.EntityLoader;
import modist.romantictp.common.instrument.ScoreTicker;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.ItemLoader;
import modist.romantictp.common.item.NaturalTrumpetItem;
import modist.romantictp.common.item.ScoreItem;
import modist.romantictp.common.sound.ServerInstrumentSoundManager;
import modist.romantictp.network.InstrumentSoundBroadcastPacket;
import modist.romantictp.network.NetworkHandler;
import modist.romantictp.network.ScoreSyncPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {
    private static final Map<Integer, ItemStack> scoreToBeFilled = new HashMap<>(); //cache on server
    private static final Map<Integer, Pair<byte[], Long>> cachedData = new HashMap<>(); //cache on server
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(
            Registries.PROCESSOR_LIST, new ResourceLocation("minecraft", "empty"));

    @SubscribeEvent
    public static void fillMidiData(AnvilRepairEvent event) {
        if (event.getOutput().getItem() instanceof ScoreItem scoreItem) {
            Player player = event.getEntity();
            if (player.level().isClientSide) {
                byte[] data = MidiFileLoader.getInstance().getMidiData(event.getOutput().getHoverName().getString());
                long time = MidiHelper.getTime(data);
                scoreItem.fillMidiData(event.getOutput(), data, time);
                NetworkHandler.sendToServer(new ScoreSyncPacket(player.getId(), data, time));
            } else {
                scoreToBeFilled.put(player.getId(), event.getOutput());
                checkFill(player.getId());
            }
        }
    }

    public static void setData(int id, byte[] data, long time) { //called from server
        cachedData.put(id, new Pair<>(data, time));
        checkFill(id);
    }

    private static void checkFill(int id) {
        if (scoreToBeFilled.containsKey(id) && cachedData.containsKey(id)) {
            if (scoreToBeFilled.get(id).getItem() instanceof ScoreItem scoreItem) {
                scoreItem.fillMidiData(scoreToBeFilled.get(id), cachedData.get(id).getFirst(), cachedData.get(id).getSecond());
            }
            scoreToBeFilled.remove(id);
            cachedData.remove(id);
        }
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity && Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            event.addCapability(new ResourceLocation(RomanticTp.MODID, "score_ticker"),
                    new ScoreTicker.ScoreTickerProvider(null));
        }
    }

    @SubscribeEvent
    public static void attachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof AutoPlayerBlockEntity blockEntity && Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            event.addCapability(new ResourceLocation(RomanticTp.MODID, "score_ticker"),
                    new ScoreTicker.ScoreTickerProvider(blockEntity));
        }
    }

    @SubscribeEvent
    public static void startUseItemEvent(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide) {
            entity.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> {
                if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND && event.getItem().getItem() instanceof InstrumentItem
                        && event.getEntity().getOffhandItem().getItem() instanceof ScoreItem scoreItem) {
                    ServerInstrumentSoundManager.getInstance().startSequence(entity, scoreItem.getMidiData(event.getEntity().getOffhandItem()));
                    scoreTicker.start(scoreItem.getTime(event.getEntity().getOffhandItem()) * 20);
                }
            });
            if (entity.getUseItem().getItem() instanceof NaturalTrumpetItem) {
                ServerInstrumentSoundManager.getInstance().playNaturalTrumpet(entity);
            }
        }
    }

    @SubscribeEvent
    public static void tickUseItemEvent(LivingEntityUseItemEvent.Tick event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide) {
            entity.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> {
                if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND && event.getItem().getItem() instanceof InstrumentItem
                        && event.getEntity().getOffhandItem().getItem() instanceof ScoreItem) {
                    scoreTicker.tick();
                    if (!scoreTicker.isPlaying()) {
                        entity.stopUsingItem();
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event) {
        if (event.getType() == EntityLoader.MUSICIAN.get()) {
            event.getTrades().get(1).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ItemLoader.SCORE.get()),
                    1, 1, 1F
            ));
        }
    }

    @SubscribeEvent
    public static void addNewVillageBuilding(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                new ResourceLocation("minecraft:village/plains/houses"),
                "romantictp:musician_house", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                new ResourceLocation("minecraft:village/snowy/houses"),
                "romantictp:musician_house", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                new ResourceLocation("minecraft:village/savanna/houses"),
                "romantictp:musician_house", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                new ResourceLocation("minecraft:village/taiga/houses"),
                "romantictp:musician_house", 5);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                new ResourceLocation("minecraft:village/desert/houses"),
                "romantictp:musician_house", 5);
    }

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry,
                                          ResourceLocation poolRL, String nbtPieceRL, int weight) {
        Holder<StructureProcessorList> emptyProcessorList = processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.legacy(nbtPieceRL,
                emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }
}
