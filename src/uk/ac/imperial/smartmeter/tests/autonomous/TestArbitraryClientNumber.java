package uk.ac.imperial.smartmeter.tests.autonomous;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.tests.GenericTest;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

public class TestArbitraryClientNumber extends GenericTest {

	@Override
	public boolean doTest() throws Exception {
		Integer nClients = 30;
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date dateStart = new Date();
		System.out.println(dateFormat.format(dateStart)); 
		ArrayList<ArraySet<ElectricityTicket>> tickets = new ArrayList<ArraySet<ElectricityTicket>>();
		try{
		for (int i = 0; i < nClients; i++)
		{
			LCStandalone newLC = new LCStandalone(9400+i, UUID.randomUUID().toString(),1.,0.09,3.);
			TicketTestHelper.bindRequirement(newLC.server.client,1.1, 20.3, 4,3.);
			clients.add(newLC);
		}
		}catch(RemoteException e)
		{
			
		}
		
		Boolean calced = false;
		int i = 0;
		while((!calced)&&(i < clients.size()))
		{
			try{
			clients.get(i).server.client.GodModeCalcTKTS();
			calced = true;
			}catch (Exception e){}
			i++;
		}
		

		
		try {
			Thread.sleep((5+nClients)*500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Double total = 0.;
		
		for (LCStandalone l : clients)
		{
			try{
			l.stop();
			tickets.add(l.server.client.getTickets());
			total += l.server.calculateTotalUtility();
			} catch (NullPointerException e){}
		}
		System.out.println(total);

		calced = false;
		i = 0;
		while((!calced)&&(i < clients.size()))
		{
			try{
			clients.get(i).wipe();
			calced = true;
			}catch (Exception e){}
			i++;
		}
		int ret = sumTkts(tickets);
		System.out.println(ret);

		Date dateEnd = new Date();
		System.out.println(dateFormat.format(dateEnd)); 
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
	public static void main(String[] args) throws Exception
	{
		TestArbitraryClientNumber x = new TestArbitraryClientNumber();
		
	}
}
