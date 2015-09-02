package uk.ac.imperial.smartmeter.tests.network;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCServer;

public class TestAppendedRequirement extends GenericTest {

	@Override
	public boolean doTest() throws Exception{
		LCServer elsie = new LCServer(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,9015,TicketTestHelper.user1,"");

		elsie.registerUser(0.,10.,0.,8999);
		TicketTestHelper.bindRequirement(elsie.client,1.1, 6.3, 4,3.);
		elsie.client.GodModeCalcTKTS();
		ArraySet<ElectricityTicket> tkt1 = elsie.client.getTickets();
		TicketTestHelper.bindRequirement(elsie.client,1.1, 6.3, 4,3.);

		elsie.client.GodModeCalcTKTS();

		ArraySet<ElectricityTicket> tkt2 = elsie.client.getTickets();
		
		elsie.client.wipeAll();
		
		try{
		return ((tkt1.getSize()==1)&&
				(tkt2.getSize()==2)&&
				((tkt1.get(0).getId().equals(tkt2.get(1).getId())
						||(tkt1.get(0).getId().equals(tkt2.get(0).getId())))));
		}
		catch (NullPointerException ex){
			return false;
		}
		catch (IndexOutOfBoundsException ex){
			return false;
		}
	}

	

}
