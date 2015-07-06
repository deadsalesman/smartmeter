package uk.ac.imperial.smartmeter.interfaces;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.db.DBManagerIFace;

public interface ControllerDBIFace<T extends UniqueIdentifierIFace> extends DBManagerIFace{
	public void pushToDB();
	public void pullFromDB();
	public void insert();
	public T extract();
	ArrayList<T> extractAll();
}
