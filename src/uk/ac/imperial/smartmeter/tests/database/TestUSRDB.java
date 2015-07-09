package uk.ac.imperial.smartmeter.tests.database;

import uk.ac.imperial.smartmeter.db.LocalSet;
import uk.ac.imperial.smartmeter.impl.HighLevelController;
import uk.ac.imperial.smartmeter.res.User;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestUSRDB extends GenericTest {

	@Override
	public boolean doTest() {


		HighLevelController l = new HighLevelController();
		String tar = l.dbUser.getPrimTable();
		l.dbUser.genericDBUpdate("DROP TABLE "+tar);
		
		HighLevelController p = new HighLevelController();
		
		l.addUser(new User("John Travolta"));
		l.pushUsrToDB();
		/*LocalSet res;
		res = p.dbUser.queryDB("SELECT * FROM " +tar);
		p.dbReq.spamLocalSet(res);*/
		
		p.pullUsrFromDB();
		
		return (p.getUsrCount()==l.getUsrCount());
	}

}
