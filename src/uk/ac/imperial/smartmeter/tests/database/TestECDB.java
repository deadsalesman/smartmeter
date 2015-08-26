package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.electronicdevices.DeviceType;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDeviceFactory;
import uk.ac.imperial.smartmeter.impl.EDController;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestECDB extends GenericTest {

	@Override
	public boolean doTest() {

		
		EDController a = new EDController();
		a.db.genericDBUpdate("DROP TABLE DEVICE_TABLE");

		EDController c = new EDController();
		EDController b = new EDController();
		
		b.addDevice(ElectronicDeviceFactory.getDevice(0),3);
		b.addDevice(ElectronicDeviceFactory.getDevice(1),5);
		b.addDevice(ElectronicDeviceFactory.getDevice(2),7);
		b.addDevice(ElectronicDeviceFactory.getDevice(3),8);
		b.pushToDB();
		
		DeviceType t = DeviceType.LED;
		b.pushToDB();
		b.setDevicesOfType(t, false);
		c.pullFromDB();

		a.db.wipe();
		return (b.getDeviceCount()==c.getDeviceCount());
	}

}
