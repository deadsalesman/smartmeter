package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestComplexTickets extends GenericTest {

	@Override
	public boolean doTest() {	
		
		
		
		LCClient aClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user1,"");
		LCClient bClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user2,"");
		LCClient cClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user3,"");
		LCClient dClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user4,"");
		LCClient eClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user5,"");
		LCClient fClient = new LCClient("localHost", 9002, "localHost", 9001,TicketTestHelper.user6,"");

		aClient.wipe();
		
		aClient.registerUser(0.,10.,0.,8000);
		bClient.registerUser(0.,10.,0.,8000);
		cClient.registerUser(0.,10.,0.,8000);
		dClient.registerUser(0.,10.,0.,8000);
		eClient.registerUser(0.,10.,0.,8000);
		fClient.registerUser(0.,10.,0.,8000);
		
		TicketTestHelper.bindRequirement(aClient,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(aClient,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(aClient,1.1, 6.3, 4,3.);
		
		TicketTestHelper.bindRequirement(bClient,3.1,4.3, 7, 2.);
		TicketTestHelper.bindRequirement(bClient,2.1, 6.3, 3, 1.);
		TicketTestHelper.bindRequirement(bClient,3.1,4.3, 7, 3.);
		TicketTestHelper.bindRequirement(bClient,2.1, 6.3, 3, 3.);
		
		TicketTestHelper.bindRequirement(cClient,3.1, 8.3, 4, 8.);
		TicketTestHelper.bindRequirement(cClient,3.1, 8.3, 4, 3.);
		
		TicketTestHelper.bindRequirement(dClient,2.1, 6.3, 3, 3.);
		TicketTestHelper.bindRequirement(dClient,3.1,4.3, 7, 3.);
		
		TicketTestHelper.bindRequirement(eClient,3.1, 8.3, 4, 3.);
		
		TicketTestHelper.bindRequirement(fClient,1.1, 6.3, 4,3.);
		TicketTestHelper.bindRequirement(fClient,3.1,4.3, 7, 3.);
		TicketTestHelper.bindRequirement(fClient,2.1, 6.3, 3, 3.);
		TicketTestHelper.bindRequirement(fClient,3.1, 8.3, 4, 3.);
		
		


		aClient.GodModeCalcTKTS();
		
		
		ArraySet<ElectricityTicket> tktsA = aClient.getTickets();
		

		aClient.wipe();
		return (tktsA.getSize()==3);
	}

}
