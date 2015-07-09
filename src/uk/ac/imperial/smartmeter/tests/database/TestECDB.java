package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.db.LocalSet;
import uk.ac.imperial.smartmeter.impl.ElectronicDeviceController;
import uk.ac.imperial.smartmeter.res.DeviceType;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestECDB extends GenericTest {

	@Override
	public boolean doTest() {

		ElectronicDeviceController a = new ElectronicDeviceController();
		a.db.genericDBUpdate("DROP TABLE DEVICE_TABLE");
		
		ElectronicDeviceController b = new ElectronicDeviceController();
		
		b.addDevice(new ElectronicDevice(true,0));
		b.addDevice(new ElectronicDevice(true,1));
		b.addDevice(new ElectronicDevice(true,2));
		b.addDevice(new ElectronicDevice(true,3));
		b.pushToDB();
		/*LocalSet res;
		res = b.db.queryDB("SELECT * FROM DEVICE_TABLE;");
		b.db.spamLocalSet(res);*/
		
		DeviceType t = DeviceType.LED;
		b.pushToDB();
		b.setDevicesOfType(t, false);
		a.pullFromDB();
		return (b.getDeviceCount()==a.getDeviceCount());
	}

}
