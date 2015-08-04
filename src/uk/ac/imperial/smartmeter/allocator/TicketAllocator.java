package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.comparators.requirementPrioComparator;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class TicketAllocator {
	private Map<ElectricityRequirement, ArrayList<QuantumNode>> reqMap;
	private CalendarQueue queue;
	private Map<ElectricityRequirement, Boolean> satMap;
	private ArraySet<UserAgent> usrArray;
	private Map<UserAgent, ArraySet<ElectricityTicket>> tickets;
	private RescherArbiter arbiter;
	private EleGenConglomerate conglom;
	private Date startDate;
	private Map<UserAgent, Double> rankings;
	private Map<UserAgent, Integer> indexes; 
	private Map<UserAgent, Boolean> userFinished; 
	private boolean tryHard;
	
	public TicketAllocator(Collection<UserAgent> collection, Date d, boolean reallocate)
	{
		reqMap = new HashMap<ElectricityRequirement, ArrayList<QuantumNode>>();
		satMap = new HashMap<ElectricityRequirement, Boolean>();
		usrArray = new ArraySet<UserAgent>();
		arbiter = new RescherArbiter();
		tickets = new HashMap<UserAgent, ArraySet<ElectricityTicket>>();
		conglom = new EleGenConglomerate();
		startDate = d;
		tryHard = reallocate;
		for (UserAgent u : collection)
		{
			addUser(u);
		}
		

		queue = new CalendarQueue(conglom,startDate);
		populateReqMap();
	}
	public void updateAgents(Collection<UserAgent> collection)
	{
		conglom = new EleGenConglomerate();
		for (UserAgent u : collection)
		{
			addUser(u);
		}
		populateReqMap();
	}
	private void populateReqMap()
	{
		for (UserAgent u : usrArray)
		{
		for (ElectricityRequirement e: u.getReqs())
		{
			if (!satMap.containsKey(e)){satMap.put(e,false);}
			if (!reqMap.containsKey(e)){reqMap.put(e, queue.findIntersectingNodes(e));}
		}
		}
	}
	private void addUser(UserAgent u)
	{
		tickets.put(u, new ArraySet<ElectricityTicket>());
		conglom.addGen(u.getGeneratedPower());
		usrArray.add(u);
	}
	private ElectricityTicket generateTicket(ElectricityRequirement e)
	{
		return new ElectricityTicket(e.getStartTime(), e.getEndTime(), e.getMaxConsumption(), e.getUserID());
	}
	public ArraySet<ElectricityTicket> getTicketsOfUser(UserAgent u)
	{
		return tickets.get(u);
	}
	private UserAgent findMaxAgent(Map<UserAgent,Double> m)
	{
		UserAgent maxAgt = null;
		Double max = -1.;
		for (Entry<UserAgent, Double> u : m.entrySet())
		{
			if ((u.getValue() > max)&&(!userFinished.containsKey(u)))
			{
				maxAgt = u.getKey();
				max = u.getValue();
			}
		}
		return maxAgt;
	}
	private Boolean updateInterferedNodes(ElectricityRequirement e, ArrayList<QuantumNode> nodes)
	{
		Boolean viable = true; //until proved otherwise;
		for (QuantumNode n : nodes)
		{
			viable &= (n.getCapacity() >= e.getConsumption(n.getStartTime()));
		}
		
		if (viable)
		{
			for (QuantumNode n : nodes)
			{
				n.addReq(e);
			}
		}
		return viable;
	}
	private Boolean genTicketIfPossible(UserAgent u, ElectricityRequirement req)
	{
		Boolean ret = false;
		ArrayList<QuantumNode> nodes = req.getTampered() ? queue.findIntersectingNodes(req) : reqMap.get(req);
		if (updateInterferedNodes(req, nodes)) { 
			ElectricityTicket ticket = generateTicket(req);
			tickets.get(u).add(ticket);
			ret = true;
		}
		return ret;
	}
	private Boolean processRequirement(UserAgent u, ElectricityRequirement req)
 {
		Boolean ret = false;
		ret = genTicketIfPossible(u, req);
		if (!ret && tryHard) {
			int count = 0;
			int attemptLimit = 3;
			while ((count <= attemptLimit) && (!ret)) {
				int offset = 1;
				QuantumNode first = queue.findIntersectingNodes(req).get(0);
				req.setStartTime(DateHelper.dPlus(first.getSoonestFinishingTime(), offset));
				ret = genTicketIfPossible(u, req);
				count++;
			}

		}

		return ret;

	}
	
	private Double findReqRatio(int offset, ArraySet<ElectricityRequirement> reqs)
	{
		ElectricityRequirement subject = reqs.get(offset);
		Double ret = subject.getMaxConsumption()*subject.getDuration()*subject.getPriority();
		Double tally = 0.;
		for (int i = offset; i < reqs.getSize(); i++)
		{
			tally += reqs.get(i).getMaxConsumption()*reqs.get(i).getDuration()*reqs.get(i).getPriority();
		}
		//System.out.println(tally + " " + ret + " " + reqs.getSize() + " " + offset);
		return ret/tally;
	}
	public Map<UserAgent, ArraySet<ElectricityTicket>> calculateTickets()
	{
		rankings = arbiter.getWeighting(usrArray);
		indexes = new HashMap<UserAgent, Integer>();
		userFinished = new HashMap<UserAgent, Boolean>();
		
		Boolean finished = false;
		
		for (UserAgent u : usrArray)
		{
			Collections.sort(u.getReqs(), new requirementPrioComparator());
			indexes.put(u, 0);
			userFinished.put(u,false);
		}
		
		while (!finished) {
			/*finds the highest priority agent, and tries to create a ticket for the highest priority requirement they have */
			finished = true;
			UserAgent max = findMaxAgent(rankings);
			try{
			ElectricityRequirement req = max.getReq(indexes.get(max));
			
			if ((!satMap.get(req))&&processRequirement(max, req))
			{
				double newRank = rankings.get(max)*(1-findReqRatio(indexes.get(max),max.getReqs()));
				rankings.put(max, newRank);
				if (newRank ==0)
				{
					userFinished.put(max, true);
				}
				satMap.put(req, true);
			}
			//System.out.println(max.getUser().getName());
			//System.out.println(max.getReqs().getSize());
			/* attempts to move on to the next requirement in the available set */
			
			if (indexes.get(max) == max.getReqs().getSize()-1)
			{
				userFinished.put(max, true);
				rankings.put(max, 0.);
			}
			else
			{

				indexes.put(max, indexes.get(max) + 1);	
			}

			}
			catch(IndexOutOfBoundsException e)
			{
				userFinished.put(max, true);
				rankings.put(max, 0.);
			}
			for (Boolean b : userFinished.values())
			{
				/*we are finished if there are no requirements left to be addressed*/
				finished &= b;
			}
		}
		return tickets;
		
	}
}
