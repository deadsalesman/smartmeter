package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.impl.EDController;
import uk.ac.imperial.smartmeter.res.DeviceType;
import uk.ac.imperial.smartmeter.res.ElectronicDevice;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestECDB extends GenericTest {

	@Override
	public boolean doTest() {

		EDController a = new EDController();
		a.db.genericDBUpdate("DROP TABLE DEVICE_TABLE");

		EDController c = new EDController();
		EDController b = new EDController();
		
		b.addDevice(new ElectronicDevice(true,0),3);
		b.addDevice(new ElectronicDevice(true,1),5);
		b.addDevice(new ElectronicDevice(true,2),7);
		b.addDevice(new ElectronicDevice(true,3),8);
		b.pushToDB();
		
		DeviceType t = DeviceType.LED;
		b.pushToDB();
		b.setDevicesOfType(t, false);
		c.pullFromDB();
		return (b.getDeviceCount()==c.getDeviceCount());
	}

}
