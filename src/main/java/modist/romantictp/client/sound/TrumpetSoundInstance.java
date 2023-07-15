package modist.romantictp.client.sound;

import modist.romantictp.RomanticTp;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrumpetSoundInstance extends AbstractTickableSoundInstance {
    private final LivingEntity player;
    private final ItemStack trumpet;
    public TrumpetSoundInstance(LivingEntity player, ItemStack trumpet, SoundEvent pSoundEvent, SoundSource pSource) {
        super(pSoundEvent, pSource, SoundInstance.createUnseededRandom());
        this.player = player;
        this.trumpet = trumpet;
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.looping = true;
    }

    @Override
    public void tick() {
        if(this.player.getUseItem()!=trumpet){
            this.stop();
            return;
        }
        for(int i=0; i<InstrumentKeyMapping.PITCHES.size(); i++){
            if(InstrumentKeyMapping.PITCHES.get(i).get().isDown()){
                this.pitch = InstrumentKeyMapping.getPitch(i);
            }
        }
    }
}
