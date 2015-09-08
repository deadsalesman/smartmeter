package uk.ac.imperial.smartmeter.tests.autonomous;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

public class TestManyStandaloneClients extends GenericTest {

	@Override
	public boolean doTest()  throws Exception{
		LCStandalone a = new LCStandalone(9304, TicketTestHelper.user1,1.,2.,3.);
		LCStandalone b = new LCStandalone(9305, TicketTestHelper.user2,2.,1.,2.);
		LCStandalone c = new LCStandalone(9306, TicketTestHelper.user3,2.,1.,3.);
		LCStandalone d = new LCStandalone(9307, TicketTestHelper.user4,1.,1.,2.);
		LCStandalone e = new LCStandalone(9308, TicketTestHelper.user5,2.,1.,1.);
		LCStandalone f = new LCStandalone(9309, TicketTestHelper.user6,1.,1.,1.);
		
		
		TicketTestHelper.bindRequirement(a.server.client,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(b.server.client,1.1, 4.3, 4,3.);
		TicketTestHelper.bindRequirement(c.server.client,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(d.server.client,1.1, 4.3, 4,3.);
		TicketTestHelper.bindRequirement(e.server.client,1.1, 2.3, 4,3.);
		TicketTestHelper.bindRequirement(f.server.client,1.1, 4.3, 4,3.);
		
		a.server.client.GodModeCalcTKTS();


		
		try {
			Thread.sleep(9000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		final ArraySet<ElectricityTicket> l = a.server.client.getTickets();
		final ArraySet<ElectricityTicket> m = b.server.client.getTickets();
		final ArraySet<ElectricityTicket> o = c.server.client.getTickets();
		final ArraySet<ElectricityTicket> p = d.server.client.getTickets();
		final ArraySet<ElectricityTicket> q = e.server.client.getTickets();
		final ArraySet<ElectricityTicket> r = f.server.client.getTickets();
		
		/*a.printTkts();
		b.printTkts();
		c.printTkts();
		d.printTkts();
		e.printTkts();
		f.printTkts();*/

		a.stop();
		b.stop();
		c.stop();
		d.stop();
		e.stop();
		f.stop();

		a.wipe();
		return (sumTkts(l,m,o,p,q,r)==6);
	}
	@SafeVarargs
	public final static Integer sumTkts(ArraySet<ElectricityTicket> ... t)
	{
		Integer count = 0;
		for (ArraySet<ElectricityTicket> a : t)
		{
			count += a.getSize();
		}
		return count;
		
	}
}
