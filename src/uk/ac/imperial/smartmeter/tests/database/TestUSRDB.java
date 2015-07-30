package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.impl.HLController;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestUSRDB extends GenericTest {

	@Override
	public boolean doTest() {


		HLController l = new HLController();
		String tar = l.dbUser.getPrimTable();
		l.dbUser.genericDBUpdate("DROP TABLE "+tar);
		
		HLController p = new HLController();
		
		l.addUser(new User("John Travolta"));
		l.pushUsrsToDB();
		
		p.pullUsrFromDB();
		
		return (p.getUsrCount()==l.getUsrCount());
	}

}
