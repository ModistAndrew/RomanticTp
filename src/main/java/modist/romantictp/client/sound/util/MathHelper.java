package modist.romantictp.client.sound.util;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class MathHelper {
    public static byte getVolumeForRelativeNotePosition(Vec3 playerPos, Vec3 notePos) {
        return getVolumeForRelativeNoteDistance(distanceBetween(playerPos, notePos));
    }

    private static byte getVolumeForRelativeNoteDistance(Double distance) {
        double volume = 127d;
        if(distance > 0) {
            volume -= Math.floor((127 * Math.pow(distance,2.5)) / (Math.pow(distance,2.5) + Math.pow(72 - distance,2.5)));
        }
        float catVolume = Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.RECORDS);
        catVolume *= Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER);
        volume *= catVolume;
        volume = volume < 0 || Double.isNaN(volume) ? 0 : Math.min(volume, 127);
        return (byte) volume;
    }

    public static byte getLRPanForRelativeNotePosition(Vec3 playerPos, Vec3 notePos, float playerHeadRotationYaw) {
        float posAngle = angleBetween(playerPos, notePos);
        float headAngle = playerHeadRotationYaw;
        headAngle = (headAngle < 0 ? headAngle + 360 : headAngle) % 360;
        double relativeAngle = (posAngle - headAngle + 630) % 360;
        double relVal = 64 * Math.sin(Math.toRadians(relativeAngle));
        int lrPan =  64 + (int)relVal;
        lrPan = lrPan < 0 ? 0 : Math.min(lrPan, 127);
        return (byte) lrPan;
    }

    private static double distanceBetween(Vec3 source, Vec3 target) {
        return source.distanceTo(target);
    }

    private static float angleBetween(Vec3 source, Vec3 target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.z() - source.z(), target.x() - source.x()));
        return (angle < 0 ? angle + 360 : angle) % 360;
    }
}
