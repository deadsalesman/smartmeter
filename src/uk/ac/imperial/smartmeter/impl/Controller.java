/*package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.db.DBManager;
import uk.ac.imperial.smartmeter.db.DBManagerIFace;
import uk.ac.imperial.smartmeter.interfaces.ControllerDBIFace;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;
import uk.ac.imperial.smartmeter.res.ArraySet;

public abstract class Controller<T extends UniqueIdentifierIFace>{
	public DBManager<T> db;
	private ArraySet<T> arr;
	
	public void pushToDB() {
		for (T i : arr)
		{
			db.insert(i);
		}
	}

	public void pullFromDB() {
		ArrayList<T>temp_array = db.extractAllDevices();
		for (T i : temp_array)
		{
			arr.add(i);
		}
	}
}
*/