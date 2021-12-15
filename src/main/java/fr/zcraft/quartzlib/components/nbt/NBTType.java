/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.zcraft.quartzlib.components.nbt;

import fr.zcraft.quartzlib.tools.PluginLogger;
import fr.zcraft.quartzlib.tools.reflection.Reflection;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


enum NBTType {
    TAG_END((byte) 0, null, Void.class),
    TAG_BYTE((byte) 1, "NBTTagByte", byte.class, Byte.class),
    TAG_SHORT((byte) 2, "NBTTagShort", short.class, Short.class),
    TAG_INT((byte) 3, "NBTTagInt", int.class, Integer.class),
    TAG_LONG((byte) 4, "NBTTagLong", long.class, Long.class),
    TAG_FLOAT((byte) 5, "NBTTagFloat", float.class, Float.class),
    TAG_DOUBLE((byte) 6, "NBTTagDouble", double.class, Double.class),
    TAG_BYTE_ARRAY((byte) 7, "NBTTagByteArray", byte[].class),
    TAG_INT_ARRAY((byte) 11, "NBTTagIntArray", int[].class),
    TAG_STRING((byte) 8, "NBTTagString", String.class),
    TAG_LIST((byte) 9, "NBTTagList", List.class),
    TAG_COMPOUND((byte) 10, "NBTTagCompound", Map.class);

    // Unique NBT type id
    private final byte id;
    private final Class[] types;
    private final String nmsClassName;
    private Class nmsClass;

    NBTType(byte id, String nmsClassName, Class... types) {
        this.id = id;
        this.types = types;
        this.nmsClassName = nmsClassName;
    }

    public static NBTType fromId(byte id) {
        for (NBTType type : NBTType.values()) {
            if (id == type.id) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal type id: " + id);
    }

    public static NBTType fromNmsNbtTag(Object nmsNbtTag) {
        try {
            //1.18
            return fromId((byte) Reflection.call(nmsNbtTag, "a"));
        } catch (Exception ex) {
            try {
                return fromId((byte) Reflection.call(nmsNbtTag, "getTypeId"));
            } catch (Exception e) {
                throw new NBTException("Unable to retrieve type of nbt tag", ex);
            }
        }
    }

    public static NBTType fromClass(Class klass) {
        for (NBTType type : NBTType.values()) {
            if (type.isAssignableFrom(klass)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Illegal type class: " + klass);
    }

    public String getNmsTagFieldName() {
        switch (this) {
            case TAG_COMPOUND:
                return "map";
            case TAG_LIST:
                return "list";
            default:
                return "data";
        }
    }

    public Class[] getJavaTypes() {
        return types;
    }

    public boolean isAssignableFrom(Class otherType) {
        for (Class type : types) {
            if (type.isAssignableFrom(otherType)) {
                return true;
            }
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public String getNmsClassName() {
        return nmsClassName;
    }

    public Class get1_17NmsClass() {
        if (nmsClassName == null) {
            return null;
        }

        try {
            if (nmsClass == null) {
                nmsClass = Reflection.getMinecraft1_17ClassByName("nbt." + nmsClassName);
            }
        } catch (Exception ex) {
            throw new NBTException("Unable to retrieve NBT tag class", ex);
        }

        return nmsClass;
    }


    public Class getNmsClass() {
        if (nmsClassName == null) {
            return null;
        }

        try {
            if (nmsClass == null) {
                nmsClass = Reflection.getMinecraftClassByName(nmsClassName);
            }
        } catch (Exception ex) {
            throw new NBTException("Unable to retrieve NBT tag class", ex);
        }

        return nmsClass;
    }

    public Object newTag(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Contents of a tag cannot be null");
        }
        if (!isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(
                    "Invalid content type '" + value.getClass() + "' for tag " + nmsClassName);
        }

        try {
            final Object tag;
            switch (this) {
                case TAG_COMPOUND:
                    tag = Reflection.instantiate(get1_17NmsClass());
                    if (value instanceof NBTCompound) {
                        setData(tag, ((NBTCompound) value).nmsNbtMap);
                    } else {
                        new NBTCompound(tag).putAll((Map) value);
                    }
                    break;

                case TAG_LIST:
                    tag = Reflection.instantiate(get1_17NmsClass());
                    if (value instanceof NBTList) {
                        setData(tag, ((NBTList) value).nmsNbtList);
                    } else {
                        new NBTList(tag).addAll((List) value);
                    }

                    // If a NBTTagList is built from scratch, the NMS object is created lately
                    // and may not have the list's type registered at this point.
                    NBTList.guessAndWriteTypeToNbtTagList(tag);
                    break;

                default:
                    Constructor cons = Reflection.findConstructor(get1_17NmsClass(), 1);
                    cons.setAccessible(true);
                    tag = cons.newInstance(value);
            }
            return tag;
        } catch (Exception e) {
            try {
                final Object tag;
                switch (this) {
                    case TAG_COMPOUND:
                        tag = Reflection.instantiate(getNmsClass());
                        if (value instanceof NBTCompound) {
                            setData(tag, ((NBTCompound) value).nmsNbtMap);
                        } else {
                            new NBTCompound(tag).putAll((Map) value);
                        }
                        break;

                    case TAG_LIST:
                        tag = Reflection.instantiate(getNmsClass());
                        if (value instanceof NBTList) {
                            setData(tag, ((NBTList) value).nmsNbtList);
                        } else {
                            new NBTList(tag).addAll((List) value);
                        }

                        // If a NBTTagList is built from scratch, the NMS object is created lately
                        // and may not have the list's type registered at this point.
                        NBTList.guessAndWriteTypeToNbtTagList(tag);
                        break;

                    default:
                        Constructor cons = Reflection.findConstructor(getNmsClass(), 1);
                        cons.setAccessible(true);
                        tag = cons.newInstance(value);
                }

                return tag;
            } catch (Exception ex) {
                throw new NBTException("Unable to create NBT tag", ex);
            }
        }
    }

    public Object getData(Object nmsNbtTag) {
        if (nmsNbtTag == null) {
            return null;
        }
        try {
            switch (getNmsTagFieldName()) {
                case "map":
                    //Since 1.17 "map" became "tags"
                    //h() return an unmodifiable map
                    return Reflection.call(nmsNbtTag.getClass(), nmsNbtTag, "h");

                case "list":
                    //We recreate the list because there are no getter for the list.
                    //TODO check if in 1.18 a getter is added for this one.
                    List list = new ArrayList();

                    for (int i = 0; i < (int) Reflection.call(nmsNbtTag.getClass(), nmsNbtTag, "size"); i++) {
                        //Strange thing of java a call cast int into integer resulting in the impossibility
                        // to call the method
                        final Method method = nmsNbtTag.getClass().getMethod("get", int.class);

                        // Cannot use Reflection.call for the same reason as above.
                        list.add(method.invoke(nmsNbtTag, i));
                    }
                    return list;
                case "data":
                    PluginLogger.info("To be implemented");
                    //TODO I don't know what to do here
                    break;
                default:
                    break;
            }
            //Fallback used for data right now don't know if this works
            return Reflection.getFieldValue(nmsNbtTag, getNmsTagFieldName());
        } catch (Exception exc) {
            //Older versions than 1.17 (fields are no longer accessible in java 17)
            try {
                return Reflection.getFieldValue(nmsNbtTag, getNmsTagFieldName());
            } catch (Exception e) {
                try {
                    return Reflection.getFieldValue(nmsNbtTag, getNmsTagFieldName());
                } catch (Exception ex) {
                    throw new NBTException("Unable to retrieve NBT tag data", ex);
                }
            }
        }


    }

    public void setData(Object nmsNbtTag, Object value) {
        try {
            switch (getNmsTagFieldName()) {
                case "map":
                    Reflection.call(nmsNbtTag.getClass(), nmsNbtTag, "set", getNmsTagFieldName(), value);
                    break;
                case "list":
                    Reflection.call(nmsNbtTag.getClass(), nmsNbtTag, "set", getNmsTagFieldName(), value);
                    break;
                case "data":
                    //TODO I don't know what to do here
                default:
                    break;
            }
        } catch (Exception e) {
            try {
                Reflection.setFieldValue(nmsNbtTag, getNmsTagFieldName(), value);
            } catch (Exception ex) {
                throw new NBTException("Unable to set NBT tag data", ex);
            }
        }

    }
}
