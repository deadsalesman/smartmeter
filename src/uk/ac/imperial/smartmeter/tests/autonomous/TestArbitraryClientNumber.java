package uk.ac.imperial.smartmeter.tests.autonomous;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

public class TestArbitraryClientNumber extends GenericTest {

	@Override
	public boolean doTest() {
		Integer nClients = 30;
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		ArrayList<ArraySet<ElectricityTicket>> tickets = new ArrayList<ArraySet<ElectricityTicket>>();
		try{
		for (int i = 0; i < nClients; i++)
		{
			LCStandalone newLC = new LCStandalone(9400+i, UUID.randomUUID().toString(),1.,200.,3.);
			TicketTestHelper.bindRequirement(newLC.server.client,1.1, 2.3, 4,3.);
			clients.add(newLC);
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}catch(RemoteException e)
		{
			
		}
		
		clients.get(0).server.client.GodModeCalcTKTS();

		
		try {
			Thread.sleep((5+nClients)*500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (LCStandalone l : clients)
		{
			l.stop();
			tickets.add(l.server.client.getTickets());
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		clients.get(0).wipe();
		int ret = sumTkts(tickets);
		System.out.println(ret);
		return ret==nClients;
	}

	private Integer sumTkts(ArrayList<ArraySet<ElectricityTicket>> tickets) {

		Integer count = 0;
		for (ArraySet<ElectricityTicket> a : tickets)
		{
			count += a.getSize();
		}
		return count;
	}

}
