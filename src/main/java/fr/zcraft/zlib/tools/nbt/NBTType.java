/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
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
package fr.zcraft.zlib.tools.nbt;

import fr.zcraft.zlib.tools.reflection.Reflection;
import java.util.List;
import java.util.Map;

enum NBTType
{
    TAG_END((byte) 0, Void.class, null),
    TAG_BYTE((byte) 1, byte.class, "NBTTagByte"),
    TAG_SHORT((byte) 2, short.class, "NBTTagShort"),
    TAG_INT((byte) 3, int.class, "NBTTagInt"),
    TAG_LONG((byte) 4, long.class, "NBTTagLong"),
    TAG_FLOAT((byte) 5, float.class, "NBTTagFloat"),
    TAG_DOUBLE((byte) 6, double.class, "NBTTagDouble"),
    TAG_BYTE_ARRAY((byte) 7, byte[].class, "NBTTagByteArray"),
    TAG_INT_ARRAY((byte) 11, int[].class, "NBTTagIntArray"),
    TAG_STRING((byte) 8, String.class, "NBTTagString"),
    TAG_LIST((byte) 9, List.class, "NBTTagList"),
    TAG_COMPOUND((byte) 10, Map.class, "NBTTagCompound");

    // Unique NBT id
    private final byte id;
    private final Class type;
    private final String nmsClassName;
    private Class nmsClass;

    private NBTType(byte id, Class type, String nmsClassName)
    {
        this.id = id;
        this.type = type;
        this.nmsClassName = nmsClassName;
    }

    public String getNmsTagFieldName()
    {
        switch (this)
        {
            case TAG_COMPOUND:
                return "map";
            case TAG_LIST:
                return "list";
            default:
                return "data";
        }
    }
    
    public Class getJavaType()
    {
        return type;
    }
    
    public int getId()
    {
        return id;
    }
    
    public String getNMSClassName()
    {
        return nmsClassName;
    }
    
    public Class getNMSClass()
    {
        try
        {
            if(nmsClass == null)
                nmsClass = Reflection.getMinecraftClassByName(nmsClassName);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Unable to retrieve NBT tag class", ex);
        }
        
        return nmsClass;
    }
    
    public Object newTag(Object value)
    {
        if(value == null)
            throw new IllegalArgumentException("Contents of a tag cannot be null");
        if(!type.isAssignableFrom(value.getClass()))
            throw new IllegalArgumentException("Invalid content type '" + value.getClass() + "' for tag : " + nmsClassName);
        
        try
        {
            Object tag;
            switch(this)
            {
                case TAG_COMPOUND:
                    tag = Reflection.instantiate(getNMSClass());
                    new NBTCompoundWrapper(tag).putAll((Map) value);
                case TAG_LIST:
                    tag = Reflection.instantiate(getNMSClass());
                    new NBTListWrapper(tag).addAll((List) value);
                default:
                    tag = Reflection.findConstructor(getNMSClass(), 1).newInstance(value);
            }
            return tag;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to create NBT tag", ex);
        }
    }
    
    public Object getData(Object nmsNBTTag)
    {
        if(nmsNBTTag == null)
            return null;
        try
        {
            return Reflection.getFieldValue(nmsNBTTag, getNmsTagFieldName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to retrieve NBT tag data", ex);
        }
    }
    
    public void setData(Object nmsNBTTag, Object value)
    {
        try
        {
            Reflection.setFieldValue(nmsNBTTag, getNmsTagFieldName(), value);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Unable to set NBT tag data", ex);
        }
    }
    
    static public NBTType fromId(byte id)
    {
        for(NBTType type : NBTType.values())
        {
            if(id == type.id)
                return type;
        }
        
        throw new IllegalArgumentException("Illegal type id : " + id);
    }
    
    static public NBTType fromNmsNbtTag(Object nmsNbtTag)
    {
        try
        {
            return fromId((byte) Reflection.call(nmsNbtTag, "getTypeId"));
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Unable to retrieve type of nbt tag", ex);
        }
    }
    
    static public NBTType fromClass(Class klass)
    {
        for(NBTType type : NBTType.values())
        {
            if(type.type.isAssignableFrom(klass))
                return type;
        }
        
        throw new IllegalArgumentException("Illegal type class : " + klass);
    }
}
