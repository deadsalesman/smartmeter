package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.comparators.demandComparator;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ArraySet<T extends UniqueIdentifierIFace> extends ArrayList<T> implements Iterable<T>, Comparable<T>, Collection<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8261194186613243995L;
	private ArrayList<T> arr;
	public ArraySet()
	{
		arr = new ArrayList<T>();
	}
	public ArraySet(T t)
	{
		arr = new ArrayList<T>();
		arr.add(t);
	}
	public boolean add(T i) {
		boolean exists = false;
		if (arr != null) {
			for (T old : arr) {
				exists |= i.getId() == old.getId();
			}
		}
		if (!exists) {
			arr.add(i);

		}
		return !exists;
		
	}
	public ArrayList<T> getAll()
	{
		return arr;
	}
	public int getSize()
	{
		return arr.size();
	}
	public void set(T t, int index)
	{
		arr.set(index, t);
	}
	public T get(int index)
	{
		return arr.get(index);
	}
	public T remove(int index) {
		T ret = null;
		try {
			ret = arr.remove(index);
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
		return ret;
	}
	public void addAll(ArraySet<T> n)
	{
		arr.addAll(n.getAll());
	}
	@Override
	public Iterator<T> iterator(){
		return arr.iterator();
	}
	@Override
	public int compareTo(T o) {
		// TODO Auto-generated method stub
		//No default sort!
		return 0;
	}
	public static void sort(ArraySet<UserAgent> users, Comparator<UserAgent> d) {
		Collections.sort(users.arr, d);
	}
	

}
