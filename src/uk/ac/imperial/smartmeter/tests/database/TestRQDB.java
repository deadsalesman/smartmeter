package uk.ac.imperial.smartmeter.tests.database;

import java.util.Date;

import uk.ac.imperial.smartmeter.db.LocalSet;
import uk.ac.imperial.smartmeter.impl.LocalController;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestRQDB extends GenericTest {

	@Override
	public boolean doTest() {

		
		LocalController l = new LocalController("Samuel Jackson");
		l.dbReq.genericDBUpdate("DROP TABLE REQUIREMENT_TABLE");
		
		LocalController p = new LocalController("John Travolta");
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(4),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(1),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(3),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(6),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(7),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(8),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(9),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(10),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(11),1,1));
		l.addRequirement(l.generateRequirement(new Date(),new Date(),new DecimalRating(1),1,1));
		l.pushToDB();
		/*LocalSet res;
		res = p.dbReq.queryDB("SELECT * FROM REQUIREMENT_TABLE;");
		p.dbReq.spamLocalSet(res);*/
		p.pullFromDB();
		
		return (l.getReqCount()==p.getReqCount());
	}

}
