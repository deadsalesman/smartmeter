package uk.ac.imperial.smartmeter.investigation;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.EDCServer;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;
import uk.ac.imperial.smartmeter.webcomms.LCAdmin;

public class Investigator {
	public Integer nAgents = 15;
	public Integer nReqs = 200;
	public Integer nTrials = 100;
	public Integer agentType = 1;
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
		//Thread.sleep(2000);
		ArrayList<Double> results = new ArrayList<Double>();
		
		for (int i = 0; i < nTrials; i++)
		{
			//results.add(this.investigateRealisticSimulation());
			results.add(this.investigateRandomPriorities());
		Thread.sleep(60000);
		}
		Double total = 0.;
		for (Double d : results)
		{
			System.out.println(d);
			total += d;
		}
	ArrayList<Double> results2 = new ArrayList<Double>();
		agentType = 3;
		for (int i = 0; i < nTrials; i++)
		{
			//results.add(this.investigateRealisticSimulation());
			results.add(this.investigateRandomPriorities());
		Thread.sleep(60000);
		}
		Double total2 = 0.;
		for (Double d : results2)
		{
			System.out.println(d);
			total += d;
		}
		System.out.println("Average = " + total / results2.size() + " for agent type 3");
		System.out.println("Average = " + total / results.size() + "for agent type 1");
		
		System.exit(0);
	}
	private Double investigateRealisticSimulation() throws Exception
	{
		ArrayList<LCStandalone> clients = InvestigationHelper.generateRealisticSimulation();
		return processRequirements(clients);
		
	}
	private Double investigateRandomPriorities() throws Exception
	{
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		for (int i = 0; i < nAgents; i++)
		{
			LCStandalone temp = InvestigationHelper.generateStandaloneSpecificDecisionProcess(i, agentType);
			InvestigationHelper.allocateManyLightsRandomPriorities(temp,nReqs, 100);
			clients.add(temp);
		}
		return processRequirements(clients);
	}
	private Double investigateManyLights() throws Exception
	{
		ArrayList<LCStandalone> clients = new ArrayList<LCStandalone>();
		for (int i = 0; i < nAgents; i++)
		{
			LCStandalone temp = InvestigationHelper.generateStandaloneSpecificDecisionProcess(i, 4);
			InvestigationHelper.allocateManyLights(temp,20, 100);
			clients.add(temp);
			System.out.println("Complete: " + i +" out of " + nAgents +" at  " +new Date());
		}
		return processRequirements(clients);

	}
	private Double processRequirements(ArrayList<LCStandalone> clients) throws Exception
	{

		ArrayList<ArraySet<ElectricityTicket>> tickets = new ArrayList<ArraySet<ElectricityTicket>>();
		Integer reqs = 0;
		System.out.println("Allocating tickets. "  + new Date());
		InvestigationHelper.allocateTickets(clients);
		System.out.println("Sleeping. "  + new Date());
		
		Integer count = 0;
		Thread.sleep(LCAdmin.sleepOnStart*3);
		for (int i = 0; i < (5+nAgents*nReqs)*300; i++)
		{
			count++;
		}
		Double total = 0.;
		Double sqTotal = 0.;
		System.out.println("Stopping clients. "  + new Date());
		for (LCStandalone l : clients)
		{
			try{
			l.stop();
			reqs += l.server.client.handler.getReqs().getSize();
			tickets.add(l.server.client.getTickets());
			Double weight = l.server.client.getUserWeight();
			Double utility = l.server.calculateTotalUtility();
			total += utility;
			sqTotal += utility*utility;
			//System.out.println("Weight: " + weight + " Utility: " + utility + " Ratio: " + weight/utility);
			} catch (NullPointerException e){ System.out.println(e.getStackTrace().toString());}
		}
		System.out.println("total utility: " + total);
		System.out.println("total stdev: " + (sqTotal-total*total)/clients.size()); //no bessel's correction because why not.
		System.out.println("total requirements: " + reqs);

		System.out.println("total trades: " +clients.get(0).server.client.printTicketTransactions());
		clients.get(0).wipe();
		int ret = sumTkts(tickets);
		System.out.println("total tkts: " + ret);
		return total;
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
