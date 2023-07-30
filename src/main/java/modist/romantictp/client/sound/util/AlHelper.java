package modist.romantictp.client.sound.util;

import modist.romantictp.RomanticTp;
import org.lwjgl.openal.AL10;

public class AlHelper {
    public static boolean checkALError() {
        int i = AL10.alGetError();
        if (i != 0) {
            RomanticTp.LOGGER.warn("OpenAL error: {}", alErrorToString(i));
            return true;
        } else {
            return false;
        }
    }

    private static String alErrorToString(int pErrorCode) {
        switch (pErrorCode) {
            case 40961:
                return "Invalid name parameter.";
            case 40962:
                return "Invalid enumerated parameter value.";
            case 40963:
                return "Invalid parameter parameter value.";
            case 40964:
                return "Invalid operation.";
            case 40965:
                return "Unable to allocate memory.";
            default:
                return "An unrecognized error occurred.";
        }
    }
}
