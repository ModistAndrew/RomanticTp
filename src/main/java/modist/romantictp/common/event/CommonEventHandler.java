package modist.romantictp.common.event;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.sound.util.MidiInfo;
import modist.romantictp.common.block.AutoPlayerBlockEntity;
import modist.romantictp.common.instrument.ScoreTicker;
import modist.romantictp.common.item.InstrumentItem;
import modist.romantictp.common.item.NaturalTrumpetItem;
import modist.romantictp.common.item.ScoreItem;
import modist.romantictp.common.sound.ServerInstrumentSoundManager;
import modist.romantictp.common.village.MusicianHouse;
import modist.romantictp.common.village.MusicianTrade;
import modist.romantictp.common.village.VillageLoader;
import modist.romantictp.network.NetworkHandler;
import modist.romantictp.network.ScoreSyncPacket;
import modist.romantictp.network.UseItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RomanticTp.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {
    private static final Map<Integer, ItemStack> scoreToBeFilled = new HashMap<>(); //cache on server
    private static final Map<Integer, MidiInfo> cachedData = new HashMap<>(); //cache on server

    @SubscribeEvent
    public static void fillMidiData(AnvilRepairEvent event) { //TODO: use our own machine instead of anvil?
        if (event.getOutput().getItem() instanceof ScoreItem scoreItem) {
            Player player = event.getEntity();
            if (player.level().isClientSide) {
                MidiInfo info = scoreItem.setMidiData(event.getOutput(), event.getOutput().getHoverName().getString());
                NetworkHandler.sendToServer(new ScoreSyncPacket(player.getId(), info));
            } else {
                scoreToBeFilled.put(player.getId(), event.getOutput());
                checkFill(player.getId());
            }
            event.getOutput().removeTagKey("display"); //remove the ugly name and set them in midiInfo
        }
    }

    public static void setData(int id, MidiInfo info) { //called from server
        cachedData.put(id, info);
        checkFill(id);
    }

    private static void checkFill(int id) {
        if (scoreToBeFilled.containsKey(id) && cachedData.containsKey(id)) {
            if (scoreToBeFilled.get(id).getItem() instanceof ScoreItem scoreItem) {
                scoreItem.fillMidiData(scoreToBeFilled.get(id), cachedData.get(id));
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
        if (!entity.level().isClientSide) {
            if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND && event.getItem().getItem() instanceof InstrumentItem
                    && event.getEntity().getOffhandItem().getItem() instanceof ScoreItem scoreItem) {
                NetworkHandler.broadcast(null, entity, new UseItemPacket(entity.getId(), true, entity.getUsedItemHand() == InteractionHand.OFF_HAND));
                entity.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> {
                    ServerInstrumentSoundManager.getInstance().startSequence(entity, scoreItem.getMidiData(event.getEntity().getOffhandItem()));
                    scoreTicker.start(scoreItem.getTime(event.getEntity().getOffhandItem()) * 20);
                });
            } else if (event.getItem().getItem() instanceof NaturalTrumpetItem) {
                NetworkHandler.broadcast(null, entity, new UseItemPacket(entity.getId(), true, entity.getUsedItemHand() == InteractionHand.OFF_HAND));
                ServerInstrumentSoundManager.getInstance().playNaturalTrumpet(entity);
            }
        }
    }

    @SubscribeEvent
    public static void tickUseItemEvent(LivingEntityUseItemEvent.Tick event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND && event.getItem().getItem() instanceof InstrumentItem
                    && event.getEntity().getOffhandItem().getItem() instanceof ScoreItem) {
                entity.getCapability(ScoreTicker.SCORE_TICKER).ifPresent(scoreTicker -> {
                    scoreTicker.tick();
                    if (!scoreTicker.isPlaying()) {
                        entity.stopUsingItem();
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void stopUseItemEvent(LivingEntityUseItemEvent.Stop event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND && event.getItem().getItem() instanceof InstrumentItem
                    && event.getEntity().getOffhandItem().getItem() instanceof ScoreItem) {
                NetworkHandler.broadcast(null, entity, new UseItemPacket(entity.getId(), false, entity.getUsedItemHand() == InteractionHand.OFF_HAND));
            } else if (event.getItem().getItem() instanceof NaturalTrumpetItem) {
                NetworkHandler.broadcast(null, entity, new UseItemPacket(entity.getId(), false, entity.getUsedItemHand() == InteractionHand.OFF_HAND));
            }
        }
    }

    @SubscribeEvent
    public static void addTrades(VillagerTradesEvent event) {
        if (event.getType() == VillageLoader.MUSICIAN.get()) {
            event.getTrades().get(1).add(MusicianTrade::sellScore);
            event.getTrades().get(1).add(MusicianTrade::buyPaper);
            event.getTrades().get(2).add(MusicianTrade::sellInstrument);
            event.getTrades().get(2).add(MusicianTrade::buyNoteBox);
            event.getTrades().get(3).add(MusicianTrade::sellInstrument);
            event.getTrades().get(3).add(MusicianTrade::sellBust);
            event.getTrades().get(4).add(MusicianTrade::sellInstrument);
            event.getTrades().get(4).add(MusicianTrade::buyDisc);
            event.getTrades().get(5).add(MusicianTrade::sellBust);
            event.getTrades().get(5).add(MusicianTrade::buyAmethyst);
        }
    }

    @SubscribeEvent
    public static void addNewVillageBuilding(ServerAboutToStartEvent event) {
        MusicianHouse.addNewVillageBuilding(event);
    }
}
