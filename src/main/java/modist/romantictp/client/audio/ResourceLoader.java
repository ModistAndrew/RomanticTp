package modist.romantictp.client.audio;

import modist.romantictp.client.audio.fork.sound.SF2Soundbank;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class ResourceLoader {
    @Nullable
    public SF2Soundbank soundbank;
    private static final ResourceLoader instance = new ResourceLoader();

    public static ResourceLoader getInstance() {
        return instance;
    }

    public void init() {
        try {
            soundbank = new SF2Soundbank(new File("C:\\Users\\zjx\\Desktop\\Music\\Touhou1.sf2"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}