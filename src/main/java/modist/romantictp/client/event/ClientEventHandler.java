package modist.romantictp.client.event;

import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.platform.InputConstants;
import modist.romantictp.RomanticTp;
import modist.romantictp.client.audio.MyChannel;
import modist.romantictp.client.keymap.InstrumentKeyMapping;
import modist.romantictp.client.sound.InstrumentSoundInstance;
import modist.romantictp.common.item.InstrumentItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void changeSound(PlaySoundSourceEvent event) {
        if(event.getSound() instanceof InstrumentSoundInstance instance){
            RomanticTp.LOGGER.info("a2:" + System.currentTimeMillis());

            ChannelAccess.ChannelHandle channelHandle = event.getEngine().instanceToChannel.get(instance);
            channelHandle.release();

            MyChannel myChannel = MyChannel.create(instance);
            myChannel.attachBufferStream(null);
            myChannel.play();

            channelHandle.stopped = false;
            channelHandle.channel = myChannel;

            if(event.getEngine().library.staticChannels instanceof Library.CountingChannelPool pool){
                pool.activeChannels.add(myChannel);
            }
            instance.bindChannel(channelHandle);
        }
    }

    @SubscribeEvent
    public static void changeSound(PlayStreamingSourceEvent event) {
//        if(processed){
//            event.getChannel().stop();
//            return;
//        }
//        processed = true;
//        try {
//            event.getChannel().stop();
//            PipedInputStream stream = KeyInput.getStream();
//            //AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File
//            //        ("C:\\Users\\zjx\\Desktop\\Music\\zjx.wav"));
//            //Clip clip = AudioSystem.getClip();
//            //clip.open(audioInputStream);
//            //clip.start();
//
//            //AudioLoader loader = new AudioLoader(audioInputStream);
//            //byte[] buffer = new byte[49328];
//            // len = audioInputStream.read(buffer);
//            //ByteBuffer buffer1 = BufferUtils.createByteBuffer(49328);
//            //buffer1.put(buffer);
////            InputStream inputstream = Minecraft.getInstance().getResourceManager().open(
////                  new ResourceLocation("sounds/records/chirp.ogg"));
////            InputStream inputstream = new FileInputStream(
////                    ("C:\\Users\\zjx\\Desktop\\ModProject\\RomanticTp\\src\\main\\resources\\assets\\romantictp\\sounds\\trumpet.ogg"));
//            event.getChannel().attachBufferStream(new MyInputStream(stream));
//            //OggAudioStream oggaudiostream = new OggAudioStream(inputstream);
//            //ByteBuffer bytebuffer = oggaudiostream.readAll();
//            //event.getChannel().attachStaticBuffer(new SoundBuffer
//            //        (AudioLoader.loadBuffer(audioInputStream), audioInputStream.getFormat()));
//            //AL10.alSourcei(event.getChannel().source, AL10.AL_BUFFER, loader.getAlBuffer());
//            //event.getChannel().setVolume(2.0F);
//            //event.getChannel().setPitch(1.0F);
//            event.getChannel().play();
//        } catch (IOException | InvalidMidiDataException | MidiUnavailableException | LineUnavailableException e) {
//            throw new RuntimeException(e);
//        }
    }

    @SubscribeEvent
    public static void startPlay(InputEvent.Key event) {
        for (int i = 0; i < 7; i++) {
            Lazy<KeyMapping> k = InstrumentKeyMapping.PITCHES.get(i);
            if (event.getKey() == k.get().getKey().getValue()) {
                LocalPlayer player = Minecraft.getInstance().player;
                ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() instanceof InstrumentItem<?> instrumentItem) {
                    if (event.getAction() == InputConstants.PRESS) {
                        instrumentItem.startPlay(player, InstrumentKeyMapping.getPitch(i), 80);
                        player.startUsingItem(InteractionHand.MAIN_HAND);
                    } else if (event.getAction() == InputConstants.RELEASE) {
                        instrumentItem.stopPlay(player, InstrumentKeyMapping.getPitch(i), 0);
                        player.stopUsingItem();
                    }
                }
            }
        }
    }
}
