package uk.ac.imperial.smartmeter.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.impl.ElectronicDevice;

public class DBTester {
	static DevicesDBManager db;
	
	public static void main(String[] args)
	{
		db = new DevicesDBManager("jdbc:sqlite:test.db");
		if (db.main(null))
		{
			System.out.println("Success");
		}
		db.initialiseDeviceEnumTable();
		ResultSet res = db.queryDB("SELECT * FROM DEVICE_TABLE;");
		db.spamResultSet(res);
		res = db.queryDB("SELECT * FROM ENUM_TABLE;");
		db.spamResultSet(res);
		ArrayList<ElectronicDevice> a = null;
		ArrayList<ElectronicDevice> b = new ArrayList<ElectronicDevice>();
		db.insertDevice(new ElectronicDevice(true,1));
		db.insertDevice(new ElectronicDevice(true,3));
		db.insertDevice(new ElectronicDevice(true,2));
		db.insertDevice(new ElectronicDevice(true,0));
		db.insertDevice(new ElectronicDevice(true,2));
		db.insertDevice(new ElectronicDevice(true,1));
		db.insertDevice(new ElectronicDevice(true,3));
		
		res = db.queryDB("SELECT * FROM DEVICE_TABLE;");
		db.spamResultSet(res);
		ElectronicDevice ed = db.extractSingleDevice(4);
		ArrayList<ElectronicDevice> arr  = db.extractMultipleDevices(2, 5);
	}
}
