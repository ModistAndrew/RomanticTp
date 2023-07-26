package modist.romantictp.common.event;

import modist.romantictp.common.item.ScoreItem;
import modist.romantictp.network.NetworkHandler;
import modist.romantictp.network.ScoreSyncPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEventHandler {
    private static final Map<Integer, ItemStack> scoreToBeFilled = new HashMap<>(); //cache on server
    private static final Map<Integer, byte[]> cachedData = new HashMap<>(); //cache on server
    @SubscribeEvent
    public static void fillMidiData(AnvilRepairEvent event) {
        if(event.getOutput().getItem() instanceof ScoreItem scoreItem) {
            Player player = event.getEntity();
            if (player.level().isClientSide) {
                byte[] data = scoreItem.fillMidiData(event.getOutput());
                NetworkHandler.sendToServer(new ScoreSyncPacket(player.getId(), data));
            } else {
                scoreToBeFilled.put(player.getId(), event.getOutput());
                checkFill(player.getId());
            }
        }
    }

    public static void setData(int id, byte[] data) { //called from server
        cachedData.put(id, data);
        checkFill(id);
    }

    private static void checkFill(int id){
        if(scoreToBeFilled.containsKey(id) && cachedData.containsKey(id)){
            if(scoreToBeFilled.get(id).getItem() instanceof ScoreItem scoreItem){
                scoreItem.fillMidiData(scoreToBeFilled.get(id), cachedData.get(id));
            }
            scoreToBeFilled.remove(id);
            cachedData.remove(id);
        }
    }
}
