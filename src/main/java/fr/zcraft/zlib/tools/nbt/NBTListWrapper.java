/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.zcraft.zlib.tools.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class NBTListWrapper implements List<Object>
{
    private final Object nmsNbtTag;
    private final List<Object> nmsNbtList;
    
    public NBTListWrapper(Object nmsListTag)
    {
        this(nmsListTag, (List<Object>) NBTType.TAG_LIST.getData(nmsListTag));
    }
    
    private NBTListWrapper(Object nmsListTag, List<Object> nmsNbtList)
    {
        this.nmsNbtTag = nmsListTag;
        this.nmsNbtList = nmsNbtList;
    }

    @Override
    public int size()
    {
        return nmsNbtList.size();
    }

    @Override
    public boolean isEmpty()
    {
        return nmsNbtList.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return nmsNbtList.contains(NBT.fromNativeValue(o));
    }

    @Override
    public Iterator<Object> iterator()
    {
        return listIterator();
    }

    @Override
    public Object[] toArray()
    {
        return toArray(new Object[]{});
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        ArrayList<T> list = new ArrayList<T>(size());
        list.addAll((List<T>) this);
        return list.toArray(a);
    }

    @Override
    public boolean add(Object e)
    {
        return nmsNbtList.add(NBT.fromNativeValue(e));
    }

    @Override
    public boolean remove(Object o)
    {
        return nmsNbtList.remove(NBT.fromNativeValue(o));
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for(Object o : c)
        {
            if(!contains(o))
                return false;
        }
        
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Object> c)
    {
        boolean changed = false;
        for(Object value : c)
        {
            if(nmsNbtList.add(NBT.fromNativeValue(value)))
                changed = true;
        }
        
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c)
    {
        int i = 0;
        for(Object o : c)
        {
            add(i + index, o);
            ++i;
        }
        
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean changed = false;
        for(Object value : c)
        {
            if(remove(value))
                changed = true;
        }
        
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean changed = false;
        
        for(Object value : this)
        {
            if(c.contains(value))
            {
                if(nmsNbtList.remove(value))
                    changed = true;
            }
        }
        
        return changed;
    }

    @Override
    public void clear()
    {
        nmsNbtList.clear();
    }

    @Override
    public Object get(int index)
    {
        return NBT.toNativeValue(nmsNbtList.get(index));
    }

    @Override
    public Object set(int index, Object element)
    {
        return NBT.toNativeValue(nmsNbtList.set(index, NBT.fromNativeValue(element)));
    }

    @Override
    public void add(int index, Object element)
    {
        nmsNbtList.add(index, NBT.fromNativeValue(element));
    }

    @Override
    public Object remove(int index)
    {
        return NBT.toNativeValue(nmsNbtList.remove(index));
    }

    @Override
    public int indexOf(Object o)
    {
        return nmsNbtList.indexOf(NBT.fromNativeValue(o));
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return nmsNbtList.lastIndexOf(NBT.fromNativeValue(o));
    }

    @Override
    public ListIterator<Object> listIterator()
    {
        return listIterator(0);
    }

    @Override
    public ListIterator<Object> listIterator(int index)
    {
        return new NBTListWrapperIterator(nmsNbtList.listIterator(index));
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex)
    {
        return new NBTListWrapper(nmsNbtTag, nmsNbtList.subList(fromIndex, toIndex));
    }
    
    private class NBTListWrapperIterator implements ListIterator<Object>
    {
        private final ListIterator<Object> iterator;
        
        public NBTListWrapperIterator(ListIterator<Object> iterator)
        {
            this.iterator = iterator;
        }
        
        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Object next()
        {
            return NBT.toNativeValue(iterator.next());
        }

        @Override
        public boolean hasPrevious()
        {
            return iterator.hasPrevious();
        }

        @Override
        public Object previous()
        {
            return NBT.toNativeValue(iterator.previous());
        }

        @Override
        public int nextIndex()
        {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex()
        {
            return iterator.previousIndex();
        }

        @Override
        public void remove()
        {
            iterator.remove();
        }

        @Override
        public void set(Object e)
        {
            iterator.set(NBT.fromNativeValue(e));
        }

        @Override
        public void add(Object e)
        {
            iterator.add(NBT.fromNativeValue(e));
        }

    }
}
