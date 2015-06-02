package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.DoubleObjectOpenHashMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectContainer;

import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;

public class DoubleObjectHashMap<T> implements Serializable, Iterable<ObjectDoublePair<T>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8751945420556178635L;
	private DoubleObjectOpenHashMap<T> g_map;
	
	public DoubleObjectHashMap()
	{
		g_map = new DoubleObjectOpenHashMap<T>();
	}
	
	public DoubleObjectHashMap(int initialCapacity)
	{
		g_map = new DoubleObjectOpenHashMap<T>(initialCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		List<ObjectIntPair<T>> list = (List<ObjectIntPair<T>>)in.readObject();
		g_map = new DoubleObjectOpenHashMap<T>(list.size());
		putAll(list);
	}

	private void writeObject(ObjectOutputStream o) throws IOException
	{
		o.writeObject(toList());
	}
	
	/** @return a list of (value, key) pairs. */
	public List<ObjectDoublePair<T>> toList()
	{
		List<ObjectDoublePair<T>> list = new ArrayList<>();
		
		for (ObjectDoublePair<T> p : this)
			list.add(p);
		
		return list;
	}
	
	public ObjectContainer<T> values()
	{
		return g_map.values();
	}
	
	/** Puts a the list of (value, key) pairs to this map. */
	public void putAll(List<ObjectIntPair<T>> list)
	{
		for (ObjectIntPair<T> p : list)
			put(p.i, (T)p.o);
	}
	
	public void put(double key, T value)
	{
		g_map.put(key, value);
	}
	
	public T get(double h_index)
	{
		return g_map.get(h_index);
	}
	
	public T remove(double key)
	{
		return g_map.remove(key);
	}
	
	public boolean containsKey(double key)
	{
		return g_map.containsKey(key);
	}
	
	public boolean isEmpty()
	{
		return g_map.isEmpty();
	}
	
	public int size()
	{
		return g_map.size();
	}
	
	public int getMaxKey()
	{
		int max = -1;
		
		for (ObjectDoublePair<T> p : this)
			max = (int) Math.max(max, p.d);
		
		return max;
	}
	
	@Override
	public String toString()
	{
		return g_map.toString();
	}
	
	@Override
	public Iterator<ObjectDoublePair<T>> iterator()
	{
		Iterator<ObjectDoublePair<T>> it = new Iterator<ObjectDoublePair<T>>()
		{
			private final int key_size = g_map.keys.length;
			private int current_index  = 0;
			
			@Override
			public boolean hasNext()
			{
				for (; current_index < key_size; current_index++)
				{
					if (g_map.allocated[current_index])
						return true;
				}
				
				return false;
			}
			
			@Override
			public ObjectDoublePair<T> next()
			{
				if (current_index < key_size)
				{
					ObjectDoublePair<T> p = new ObjectDoublePair<T>(g_map.values[current_index], g_map.keys[current_index]);
					current_index++;
					return p;
				}
				
				return null;
			}
			
			@Override
			public void remove() {}
		};
				
		return it;
	}
	
	
	

}
