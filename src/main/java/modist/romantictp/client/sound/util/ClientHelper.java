package modist.romantictp.client.sound.util;

import modist.romantictp.client.config.RomanticTpConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;


public class ClientHelper {
    public static boolean nearToLocalPlayer(Vec3 pos) { //check whether to load or unload sound
        return Minecraft.getInstance().player.position().distanceTo(pos)
                <= RomanticTpConfig.MAX_LOAD_DISTANCE.get();
    }
}
