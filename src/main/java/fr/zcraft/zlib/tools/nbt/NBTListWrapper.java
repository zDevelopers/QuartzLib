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
import java.util.NoSuchElementException;

class NBTListWrapper implements List<Object>
{
    private final Object nmsNbtTag;
    private List<Object> nmsNbtList;
    
    public NBTListWrapper(Object nmsListTag)
    {
        this(nmsListTag, (List<Object>) NBTType.TAG_LIST.getData(nmsListTag));
    }
    
    private NBTListWrapper(Object nmsListTag, List<Object> nmsNbtList)
    {
        this.nmsNbtTag = nmsListTag;
        this.nmsNbtList = nmsNbtList;
    }
    
    private List<Object> getNbtList()
    {
        if(nmsNbtList == null)
        {
            nmsNbtList = new ArrayList<Object>();
            NBTType.TAG_COMPOUND.setData(nmsNbtTag, nmsNbtList);
        }
        
        return nmsNbtList;
    }

    @Override
    public int size()
    {
        if(nmsNbtList == null) return 0;
        return nmsNbtList.size();
    }

    @Override
    public boolean isEmpty()
    {
        if(nmsNbtList == null) return true;
        return nmsNbtList.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        if(nmsNbtList == null) return false;
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
        if(nmsNbtList != null)
            list.addAll((List<T>) this);
        return list.toArray(a);
    }

    @Override
    public boolean add(Object e)
    {
        return getNbtList().add(NBT.fromNativeValue(e));
    }

    @Override
    public boolean remove(Object o)
    {
        if(nmsNbtList == null) return false;
        return nmsNbtList.remove(NBT.fromNativeValue(o));
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        if(nmsNbtList == null) return false;
        
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
            if(getNbtList().add(NBT.fromNativeValue(value)))
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
        if(nmsNbtList == null) return false;
        
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
        if(nmsNbtList == null) return false;
        
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
        if(nmsNbtList == null) return;
        
        nmsNbtList.clear();
    }

    @Override
    public Object get(int index)
    {
        if(nmsNbtList == null)
            throw new ArrayIndexOutOfBoundsException("NBT List is empty");
        return NBT.toNativeValue(nmsNbtList.get(index));
    }

    @Override
    public Object set(int index, Object element)
    {
        return NBT.toNativeValue(getNbtList().set(index, NBT.fromNativeValue(element)));
    }

    @Override
    public void add(int index, Object element)
    {
        getNbtList().add(index, NBT.fromNativeValue(element));
    }

    @Override
    public Object remove(int index)
    {
        if(nmsNbtList == null)
            throw new IndexOutOfBoundsException("NBT List is empty");
        return NBT.toNativeValue(nmsNbtList.remove(index));
    }

    @Override
    public int indexOf(Object o)
    {
        if(nmsNbtList == null)
            return -1;
        return nmsNbtList.indexOf(NBT.fromNativeValue(o));
    }

    @Override
    public int lastIndexOf(Object o)
    {
        if(nmsNbtList == null)
            return -1;
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
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException("Index is out of bounds : " + index);
        
        if(nmsNbtList == null)
            return new NBTListWrapperIterator(null);
        
        return new NBTListWrapperIterator(nmsNbtList.listIterator(index));
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex)
    {
        if (fromIndex < 0 || fromIndex > size())
            throw new IndexOutOfBoundsException("fromIndex is out of bounds : " + fromIndex);
        
        if (toIndex < 0 || toIndex > size())
            throw new IndexOutOfBoundsException("toIndex is out of bounds : " + toIndex);
        
        if(nmsNbtList == null)
            return new NBTListWrapper(nmsNbtTag, null);
        
        return new NBTListWrapper(nmsNbtTag, nmsNbtList.subList(fromIndex, toIndex));
    }
    
    private class NBTListWrapperIterator implements ListIterator<Object>
    {
        private ListIterator<Object> iterator;
        
        public NBTListWrapperIterator(ListIterator<Object> iterator)
        {
            this.iterator = iterator;
        }
        
        private ListIterator<Object> getIterator()
        {
            if(iterator == null)
            {
                iterator = getNbtList().listIterator();
            }
            
            return iterator;
        }
        
        @Override
        public boolean hasNext()
        {
            if(iterator == null)
                return false;
            return iterator.hasNext();
        }

        @Override
        public Object next()
        {
            if(iterator == null)
                throw new NoSuchElementException("NBT List is empty");
            return NBT.toNativeValue(iterator.next());
        }

        @Override
        public boolean hasPrevious()
        {
            if(iterator == null)
                return false;
            return iterator.hasPrevious();
        }

        @Override
        public Object previous()
        {
            if(iterator == null)
                throw new NoSuchElementException("NBT List is empty");
            return NBT.toNativeValue(iterator.previous());
        }

        @Override
        public int nextIndex()
        {
            if(iterator == null)
                throw new NoSuchElementException("NBT List is empty");
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex()
        {
            if(iterator == null)
                throw new NoSuchElementException("NBT List is empty");
            return iterator.previousIndex();
        }

        @Override
        public void remove()
        {
            if(iterator == null)
                throw new NoSuchElementException("NBT List is empty");
            iterator.remove();
        }

        @Override
        public void set(Object e)
        {
            getIterator().set(NBT.fromNativeValue(e));
        }

        @Override
        public void add(Object e)
        {
            getIterator().add(NBT.fromNativeValue(e));
        }

    }
}
