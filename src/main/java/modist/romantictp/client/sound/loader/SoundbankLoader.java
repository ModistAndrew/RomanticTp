package modist.romantictp.client.sound.loader;

import modist.romantictp.client.sound.fork.gervill.SF2Soundbank;

import javax.annotation.Nullable;

public class SoundbankLoader {
    @Nullable
    public SF2Soundbank soundbank;
    private static final SoundbankLoader instance = new SoundbankLoader();

    public static SoundbankLoader getInstance() {
        return instance;
    }

    public void init() {
//        try {
//            soundbank = new SF2Soundbank(new File("C:\\Users\\zjx\\Desktop\\Music\\Touhou1.sf2"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}