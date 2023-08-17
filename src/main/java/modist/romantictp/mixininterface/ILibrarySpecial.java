package modist.romantictp.mixininterface;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;

import javax.annotation.Nullable;

public interface ILibrarySpecial {
    @Nullable
    Channel romanticTp$acquireChannelSpecial(Library.Pool pPool);
}
