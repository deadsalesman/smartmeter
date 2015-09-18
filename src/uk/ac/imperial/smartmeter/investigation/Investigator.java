package uk.ac.imperial.smartmeter.investigation;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.EDCServer;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;

public class Investigator {
	public Integer nAgents = 15;
	public Boolean selfHost = true;
	Investigator()
	{
		try{
		investigate();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public void investigate() throws Exception
	{
	if (selfHost)
	{
		HLCServer h  = new HLCServer(9001);
		Thread hThread = new Thread(h);
		EDCServer e = new EDCServer(9002);
		Thread eThread = new Thread(e);
		
		hThread.start();
		eThread.start();
	}
		Thread.sleep(2000);
		this.investigateRandomPriorities();
		System.exit(0);
	}
	private void investigateRandomPriorities() throws Exception
	{
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		for (int i = 0; i < nAgents; i++)
		{
			LCStandalone temp = InvestigationHelper.generateStandaloneSpecificDecisionProcess(i, 1);
			InvestigationHelper.allocateManyLightsRandomPriorities(temp,40, 100);
			clients.add(temp);
		}
		processRequirements(clients);
	}
	private void investigateManyLights() throws Exception
	{
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		for (int i = 0; i < nAgents; i++)
		{
			LCStandalone temp = InvestigationHelper.generateStandaloneSpecificDecisionProcess(i, 4);
			InvestigationHelper.allocateManyLights(temp,20, 100);
			clients.add(temp);
		}
		processRequirements(clients);

	}
	private void processRequirements(ArrayList<LCStandalone> clients) throws Exception
	{

		ArrayList<ArraySet<ElectricityTicket>> tickets = new ArrayList<ArraySet<ElectricityTicket>>();
		Integer reqs = 0;
		System.out.println("Allocating tickets.");
		InvestigationHelper.allocateTickets(clients);
		System.out.println("Sleeping.");
		
		Integer count = 0;
		for (int i = 0; i < (5+nAgents)*800; i++)
		{
			count++;
		}
		Double total = 0.;
		System.out.println("Stopping clients.");
		for (LCStandalone l : clients)
		{
			try{
			l.stop();
			reqs += l.server.client.handler.getReqs().getSize();
			tickets.add(l.server.client.getTickets());
			total += l.server.calculateTotalUtility();
			} catch (NullPointerException e){}
		}
		System.out.println("total utility: " + total);
		System.out.println("total requirements: " + reqs);

		System.out.println("total trades: " +clients.get(0).server.client.printTicketTransactions());
		clients.get(0).wipe();
		int ret = sumTkts(tickets);
		System.out.println("total tkts: " + ret);
	}
	private Integer sumTkts(ArrayList<ArraySet<ElectricityTicket>> tickets) {

		Integer count = 0;
		for (ArraySet<ElectricityTicket> a : tickets)
		{
			for (ElectricityTicket t : a)
			{
				if (t!=null)
				{
					count++;
				}
			}
		}
		return count;
	}
	public static void main(String[] args) throws Exception
	{
		Investigator x = new Investigator();
		
	}
}
