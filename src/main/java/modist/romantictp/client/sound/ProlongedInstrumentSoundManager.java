package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;

public class ProlongedInstrumentSoundManager extends InstrumentSoundManager{
    private int status;
    private int time;

    public void tick() {
        ++this.time;
    }
}
