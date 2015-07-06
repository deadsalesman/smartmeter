package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ArraySet<T extends UniqueIdentifierIFace> implements Iterable<T>{
	private ArrayList<T> arr;
	public ArraySet()
	{
		arr = new ArrayList<T>();
	}
	public void add(T i) {
		boolean exists = false;
		if (arr != null) {
			for (T old : arr) {
				exists |= i.getId() == old.getId();
			}
		}
		if (!exists) {
			arr.add(i);

		}
		
	}
	public T get(int index)
	{
		return arr.get(index);
	}
	public void remove(int index) {
		try {
		arr.remove(index);
		}
		catch (IndexOutOfBoundsException e){
			System.err.println("IndexOutOfBoundsException: " + e.getMessage() + " in ElectronicDeviceController");
		}
	}
	@Override
	public Iterator<T> iterator(){
		return arr.iterator();
	}

}
