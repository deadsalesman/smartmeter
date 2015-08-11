package uk.ac.imperial.smartmeter.tests.network;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.webcomms.DefaultTestClient;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TestHLCGetTkt extends GenericTest {

	@Override
	public boolean doTest() {
		String t = UUID.randomUUID().toString();
		LCClient elsie = new LCClient(DefaultTestClient.ipAddr, DefaultTestClient.EDCPort, DefaultTestClient.ipAddr,DefaultTestClient.HLCPort,t,"");
		ElectricityRequirement e = new ElectricityRequirement(
				DateHelper.os(0.),
				DateHelper.os(10.),
				new DecimalRating(2),
				1,
				1,
				elsie.getId(),
				UUID.randomUUID().toString()
				);
		elsie.registerUser(0.,10.,0.,8000);
		elsie.setRequirement(e);
		elsie.GodModeCalcTKTS();
		ArraySet<ElectricityTicket> tkt = elsie.getTickets();
		try{
		Boolean ret = tkt.get(0).ownerID.toString().equals(elsie.getId());
		elsie.wipeAll();
		return ret;
		}
		catch (NullPointerException ex){
			return false;
		}
		catch (IndexOutOfBoundsException ex){
			return false;
		}
	}

}
