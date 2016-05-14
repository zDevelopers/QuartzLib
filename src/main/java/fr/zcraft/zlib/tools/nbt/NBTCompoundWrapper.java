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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class NBTCompoundWrapper implements Map<String, Object>
{
    private final Object nmsNbtTag;
    private Map<String, Object> nmsNbtMap;
    
    public NBTCompoundWrapper(Object nativeNBTTag)
    {
        this.nmsNbtTag = nativeNBTTag;
        this.nmsNbtMap = (Map<String, Object>) NBTType.TAG_COMPOUND.getData(nmsNbtTag);
    }
    
    private Map<String, Object> getNbtMap()
    {
        if(nmsNbtMap == null)
        {
            nmsNbtMap = new HashMap<String, Object>();
            NBTType.TAG_COMPOUND.setData(nmsNbtTag, nmsNbtMap);
        }
        
        return nmsNbtMap;
    }
    
    @Override
    public int size()
    {
        if(nmsNbtMap == null) return 0;
        return nmsNbtMap.size();
    }

    @Override
    public boolean isEmpty()
    {
        if(nmsNbtMap == null) return true;
        return nmsNbtMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key)
    {
        if(nmsNbtMap == null) return false;
        return nmsNbtMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        if(nmsNbtMap == null) return false;
        return nmsNbtMap.containsValue(NBT.fromNativeValue(value));
    }

    @Override
    public Object get(Object key)
    {
        if(nmsNbtMap == null) return null;
        return NBT.toNativeValue(nmsNbtMap.get(key));
    }

    @Override
    public Object put(String key, Object value)
    {
        return NBT.toNativeValue(getNbtMap().put(key, NBT.fromNativeValue(value)));
    }

    @Override
    public Object remove(Object key)
    {
        if(nmsNbtMap == null) return null;
        return NBT.toNativeValue(nmsNbtMap.remove(key));
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m)
    {
        for(Entry<? extends String, ? extends Object> entry : m.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear()
    {
        if(nmsNbtMap == null) return;
        nmsNbtMap.clear();
    }

    @Override
    public Set<String> keySet()
    {
        if(nmsNbtMap == null) return new HashSet<String>();
        return nmsNbtMap.keySet();
    }

    @Override
    public Collection<Object> values()
    {
        ArrayList list = new ArrayList(size());
        if(nmsNbtMap == null) return list;
        
        for(Object value : nmsNbtMap.values())
        {
            list.add(NBT.toNativeValue(value));
        }
        return list;
    }

    @Override
    public Set<Entry<String, Object>> entrySet()
    {
        HashSet<Entry<String,Object>> set = new HashSet(size());
        if(nmsNbtMap == null) return set;
        
        for(Entry<String, Object> entry : nmsNbtMap.entrySet())
        {
            set.add(new NBTCompoundWrapperEntry(entry.getKey()));
        }
        
        return set;
    }
    
    private class NBTCompoundWrapperEntry implements Map.Entry<String, Object>
    {
        private final String key;
        
        public NBTCompoundWrapperEntry(String key)
        {
            this.key = key;
        }
        
        @Override
        public String getKey()
        {
            return key;
        }

        @Override
        public Object getValue()
        {
            return get(key);
        }

        @Override
        public Object setValue(Object value)
        {
            return put(key, value);
        }
        
    }

}
