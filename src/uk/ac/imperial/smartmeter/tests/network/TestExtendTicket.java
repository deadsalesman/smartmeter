package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestExtendTicket extends GenericTest {

	@Override
	public boolean doTest()  throws Exception{
		
		LCServer aClient = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9009,TicketTestHelper.user1,"");
		
		aClient.start();
		
		aClient.registerUser(0.,10.,0.,aClient.getPort());
		
		TicketTestHelper.bindRequirement(aClient.client,6.1, 9.3, 4,3.);
		TicketTestHelper.bindRequirement(aClient.client,6.1, 9.3, 4,3.);
		
		aClient.client.GodModeCalcTKTS();
		
		final ArraySet<ElectricityTicket> a = aClient.client.getTickets();
		aClient.setTicketDurationModifiable(true);

		ElectricityRequirement req = aClient.client.handler.getReqs().get(0);
		req.setStartTime(req.getStartTime(), req.getDuration()*2);
		aClient.client.extendMutableTicket(a.get(0),a.get(1),req);
		
		aClient.client.wipeAll();
		aClient.close();
		double o = a.get(0).getQuantisedDuration();
		double e = req.getDuration();
		return (Math.abs(o-e)<2);
	}

}
