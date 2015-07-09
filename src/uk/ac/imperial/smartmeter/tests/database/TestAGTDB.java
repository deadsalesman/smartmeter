package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.db.LocalSet;
import uk.ac.imperial.smartmeter.impl.HighLevelController;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestAGTDB extends GenericTest {

	@Override
	public boolean doTest() {

		HighLevelController l = new HighLevelController();
		String tar = l.dbAgt.getPrimTable();
		l.dbAgt.genericDBUpdate("DROP TABLE "+tar);
		
		HighLevelController p = new HighLevelController();
		
		l.addUserAgent(new UserAgent(new User("John Travolta"),1.,2.,3.,4.));
		l.addUserAgent(new UserAgent(new User("Uma Thurman"),8.,7.,6.,5.));
		l.addUserAgent(new UserAgent(new User("Samuel Jackson"),9.,10.,11.,12.));
		l.pushAgtToDB();
		l.pushUsrToDB();
		/*LocalSet res;
		res = p.dbAgt.queryDB("SELECT * FROM " +tar);
		p.dbReq.spamLocalSet(res);*/
		
		p.pullAgtFromDB();
		
		return (p.getAgtCount()==l.getAgtCount());
	}

}
