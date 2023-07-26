package modist.romantictp.client.sound.util;

import modist.romantictp.RomanticTp;
import net.minecraft.client.Minecraft;
import org.apache.commons.compress.utils.FileNameUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHelper {

    @Nullable
    public static File getFile(String folderName, String fileName) {
        RomanticTp.info(FileHelper.class.getResource(""));
        RomanticTp.info(FileHelper.class.getClassLoader().getResource("/"));
        RomanticTp.info(FileHelper.class.getClassLoader().getResource(""));
        URL dirURL = FileHelper.class.getResource("/assets/romantictp/" + folderName + "/" + fileName);
        File file = new File(dirURL.getFile());
        return file.canRead() ? file : null;
    }
}
