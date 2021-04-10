package fr.zcraft.quartzlib.components.nbt;

import fr.zcraft.quartzlib.tools.reflection.Reflection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class NBTReadFile {
    public Object tagCompound;

    public void read(File file) {
        try {
            // if the file exists we read it
            if (file.exists()) {
                FileInputStream fileinputstream = new FileInputStream(file);

                tagCompound = Reflection.getMinecraftClassByName("NBTCompressedStreamTools")
                        .getMethod("a", File.class).invoke(null, file);
                fileinputstream.close();

            } else {
                // else we create an empty TagCompound
                clear();
            }
        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void write(File file, Object nbtTagCompound) {
        try {

            if (!file.exists()) {
                file.createNewFile();
            }


            tagCompound = Reflection.getMinecraftClassByName("NBTCompressedStreamTools")
                    .getMethod("a", Reflection.getMinecraftClassByName("NBTTagCompound"), File.class)
                    .invoke(null, nbtTagCompound, file);


        } catch (IOException | ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        tagCompound = new NBTCompound();
    }
}
