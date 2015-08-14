package uk.ac.imperial.smartmeter.tests.autonomous;

import java.util.UUID;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

public class TestObservableFlow extends GenericTest {

	@Override
	public boolean doTest() {
		LCStandalone a = new LCStandalone(9302, TicketTestHelper.user1,1.,2.,3.);
		LCStandalone b = new LCStandalone(9303, TicketTestHelper.user2,2.,1.,2.);
		
		
		
		TicketTestHelper.bindRequirement(a.server.client,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(b.server.client,1.1, 4.3, 4,3.);
		
		a.server.client.GodModeCalcTKTS();

		final ArraySet<ElectricityTicket> l = a.server.client.getTickets();
		final ArraySet<ElectricityTicket> m = b.server.client.getTickets();
		UUID lID = UUID.fromString(l.get(0).id.toString());
		UUID mID = UUID.fromString(m.get(0).id.toString());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		a.stop();
		b.stop();
		
		ArraySet<ElectricityTicket> p = a.server.client.getTickets();
		ArraySet<ElectricityTicket> q = b.server.client.getTickets();
		
		Boolean temp = (p.get(0).getId().equals(mID.toString()))&&(q.get(0).getId().equals(lID.toString()));
		a.wipe();
		return temp;
	}

}
