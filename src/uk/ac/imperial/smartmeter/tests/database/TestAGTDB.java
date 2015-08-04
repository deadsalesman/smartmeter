package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.impl.HLController;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestAGTDB extends GenericTest {

	@Override
	public boolean doTest() {

		HLController d = new HLController();
		d.dbAgt.dropHostedTable();
		d.dbReq.dropHostedTable();
		d.dbUser.dropHostedTable();
		
		HLController l = new HLController();
		
		l.addUserAgent(new UserAgent(new User("John Travolta"),1.,2.,3.,4.));
		l.addUserAgent(new UserAgent(new User("Uma Thurman"),8.,7.,6.,5.));
		l.addUserAgent(new UserAgent(new User("Samuel Jackson"),9.,10.,11.,12.));
		

		HLController p = new HLController();
		p.dbAgt.dropHostedTable();
		p.dbReq.dropHostedTable();
		p.dbUser.dropHostedTable();
		return (p.getAgtCount()==l.getAgtCount());
	}

}
