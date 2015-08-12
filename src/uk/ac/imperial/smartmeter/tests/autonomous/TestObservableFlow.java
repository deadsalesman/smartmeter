package uk.ac.imperial.smartmeter.tests.autonomous;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

public class TestObservableFlow extends GenericTest {

	@Override
	public boolean doTest() {
		LCStandalone a = new LCStandalone(9300, TicketTestHelper.user1,1.,2.,3.);
		LCStandalone b = new LCStandalone(9301, TicketTestHelper.user2,2.,1.,2.);
		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		a.stop();
		b.stop();
		a.wipe();
		return false;
	}

}
