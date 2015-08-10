package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestAppendedRequirement extends GenericTest {

	@Override
	public boolean doTest() {
		String t = UUID.randomUUID().toString();
		LCClient elsie = new LCClient("localHost", 9002, "localHost", 9001,t,"");

		elsie.registerUser(0.,10.,0.,8999);
		TicketTestHelper.bindRequirement(elsie,1.1, 6.3, 4,3.);
		elsie.GodModeCalcTKTS();
		ArraySet<ElectricityTicket> tkt1 = elsie.getTickets();
		TicketTestHelper.bindRequirement(elsie,1.1, 6.3, 4,3.);

		elsie.GodModeCalcTKTS();

		ArraySet<ElectricityTicket> tkt2 = elsie.getTickets();
		
		elsie.wipe();
		
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
