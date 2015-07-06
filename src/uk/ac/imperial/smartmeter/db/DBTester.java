package uk.ac.imperial.smartmeter.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.smartmeter.impl.ElectronicDevice;
import uk.ac.imperial.smartmeter.impl.ElectronicDeviceController;
import uk.ac.imperial.smartmeter.impl.LocalController;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.DeviceType;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

public class DBTester {
	
	public static void main(String[] args)
	{
		//testECDB();
		testRQDB();
	}
private static void testECDB()
{
	LocalSet res;
	ElectronicDeviceController a = new ElectronicDeviceController();
	
	ElectronicDeviceController b = new ElectronicDeviceController();
	b.addDevice(new ElectronicDevice(true,0));
	b.addDevice(new ElectronicDevice(true,1));
	b.addDevice(new ElectronicDevice(true,2));
	b.addDevice(new ElectronicDevice(true,3));
	b.pushToDB();
	res = b.db.queryDB("SELECT * FROM DEVICE_TABLE;");
	b.db.spamLocalSet(res);
	
	DeviceType t = DeviceType.LED;
	b.pushToDB();
	b.setDevicesOfType(t, false);
	a.pullFromDB();
	
	int e =3;
}
private static void testRQDB()
{
	LocalSet res;
	LocalController l = new LocalController();
	LocalController p = new LocalController();
	l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(4),1,1));
	l.pushToDB();
	p.pullFromDB();
	
}
}

