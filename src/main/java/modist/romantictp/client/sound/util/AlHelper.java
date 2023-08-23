package modist.romantictp.client.sound.util;

import modist.romantictp.RomanticTp;
import org.lwjgl.openal.AL10;

public class AlHelper {
    public static boolean checkALError() {
        int i = AL10.alGetError();
        if (i != 0) {
            RomanticTp.LOGGER.error("OpenAL error: {}", alErrorToString(i));
            return true;
        } else {
            return false;
        }
    }

    private static String alErrorToString(int pErrorCode) {
        return switch (pErrorCode) {
            case 40961 -> "Invalid name parameter.";
            case 40962 -> "Invalid enumerated parameter value.";
            case 40963 -> "Invalid parameter parameter value.";
            case 40964 -> "Invalid operation.";
            case 40965 -> "Unable to allocate memory.";
            default -> "An unrecognized error occurred.";
        };
    }

    @SuppressWarnings("unused")
    public static void printSource(int source) {
        RomanticTp.LOGGER.info("Status of {}: {}", source, AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE));
        RomanticTp.LOGGER.info("Queued buffers of {}: {}", source, AL10.alGetSourcei(source, AL10.AL_BUFFERS_QUEUED));
        RomanticTp.LOGGER.info("Processed buffers of {}: {}", source, AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED));
        RomanticTp.LOGGER.info("Gain of {}: {}", source, AL10.alGetSourcef(source, AL10.AL_GAIN));
        float[] f1 = new float[1];
        float[] f2 = new float[1];
        float[] f3 = new float[1];
        AL10.alGetSource3f(source, AL10.AL_POSITION, f1, f2, f3);
        RomanticTp.LOGGER.info("Position of {}: {} {} {}", source, f1[0], f2[0], f3[0]);
        AL10.alGetListener3f( AL10.AL_POSITION, f1, f2, f3);
        RomanticTp.LOGGER.info("Listener Position of {}: {} {} {}", source, f1[0], f2[0], f3[0]);
        checkALError();
    }
}
