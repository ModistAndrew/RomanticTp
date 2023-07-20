package modist.romantictp.client.sound;

import modist.romantictp.common.instrument.Instrument;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class InstrumentSoundInstance extends AbstractTickableSoundInstance {
    private final Instrument instrument;
    private LivingEntity player;
    private static final int MAX_LEFT_TICK = 10;
    private int leftTick = MAX_LEFT_TICK;
    public ChannelAccess.ChannelHandle channelHandle;

    public InstrumentSoundInstance(LivingEntity player, Instrument instrument) {
        super(instrument.sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.instrument = instrument;
        this.player = player;
    }

    public void bindChannel(ChannelAccess.ChannelHandle channelHandle){
        this.channelHandle = channelHandle;
    }
    @Override
    public void tick() {
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }
}