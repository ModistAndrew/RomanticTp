package modist.romantictp.data;

import modist.romantictp.RomanticTp;
import modist.romantictp.common.sound.SoundEventLoader;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class SoundDefinitionsGenerator extends SoundDefinitionsProvider {

    protected SoundDefinitionsGenerator(PackOutput output, ExistingFileHelper helper) {
        super(output, RomanticTp.MODID, helper);
    }

    @Override
    public void registerSounds() {
        SoundEventLoader.SOUND_MAP.forEach((s, r) ->
                this.add(r.getId(), definition()
                        .subtitle("sound.romantictp." + s)
                        .with(sound(new ResourceLocation(RomanticTp.MODID, s))))
        );
    }
}
