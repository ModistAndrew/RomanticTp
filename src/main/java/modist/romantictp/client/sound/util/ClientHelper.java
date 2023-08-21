package modist.romantictp.client.sound.util;

import modist.romantictp.client.config.RomanticTpConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;


public class ClientHelper {
    public static boolean nearToLocalPlayer(Vec3 pos) {
        return Minecraft.getInstance().player.position().distanceTo(pos) <= RomanticTpConfig.MAX_DISTANCE.get();
    }

    public static boolean nearToLocalPlayer(Entity entity) {
        return nearToLocalPlayer(entity.position());
    }
}
