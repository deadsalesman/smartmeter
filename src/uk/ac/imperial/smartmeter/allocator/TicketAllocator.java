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
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;

public class TicketAllocator {
	private Map<ElectricityRequirement, ArrayList<QuantumNode>> reqMap;
	private CalendarQueue queue;
	private ArraySet<UserAgent> usrArray;
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
		arbiter = new RescherArbiter();
		conglom = new EleGenConglomerate();
		usrArray = (ArraySet<UserAgent>) collection;
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
		for (ElectricityRequirement e: u.getReqTktMap().keySet())
		{
			if (!reqMap.containsKey(e)){reqMap.put(e, queue.findIntersectingNodes(e));}
		}
		}
	}
	private void addUser(UserAgent u)
	{
		conglom.addGen(u.getGeneratedPower());
		usrArray.add(u);
	}
	private ElectricityTicket generateTicket(ElectricityRequirement e)
	{
		return new ElectricityTicket(e.getStartTime(), e.getEndTime(), e.getMaxConsumption(), e.getUserID(), e.getId());
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
	private Boolean removeReqFromNodes(ElectricityRequirement e, ArrayList<QuantumNode> nodes)
	{
		Boolean ret = true; //innocent until proven guilty
		for (QuantumNode n : nodes)
		{
			ret &= n.removeReq(e.getId());
		}
		return ret;
	}
	private Boolean addReqToNodes(ElectricityRequirement e, ArrayList<QuantumNode> nodes)
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
		if (addReqToNodes(req, nodes)) { 
			ElectricityTicket ticket = generateTicket(req);
			u.getReqTktMap().put(req, ticket);
			ret = true;
		}
		return ret;
	}
	private Boolean genTicketIfPossible(UserAgent u, ElectricityRequirement req, ElectricityRequirement proxy)
	{
		Boolean ret = false;
		ArrayList<QuantumNode> nodes = queue.findIntersectingNodes(proxy);
		if (addReqToNodes(proxy, nodes)) { 
			ElectricityTicket ticket = generateTicket(proxy);
			u.getReqTktMap().put(req, ticket);
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
			int offset = 1;
			ElectricityRequirement proxy = new ElectricityRequirement(req);
			while ((count <= attemptLimit) && (!ret)) {
				QuantumNode first = queue.findIntersectingNodes(proxy).get(0);
				proxy.setStartTime(DateHelper.dPlus(first.getSoonestFinishingTime(), offset));
				ret = genTicketIfPossible(u, req, proxy);
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
	public ArraySet<UserAgent> calculateTickets()
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
			
			if ((max.getReqTktMap().get(req)==null)&&(processRequirement(max, req)))
			{
				double newRank = rankings.get(max)*(1-findReqRatio(indexes.get(max),max.getReqs()));
				rankings.put(max, newRank);
				if (newRank ==0)
				{
					userFinished.put(max, true);
				}
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
		return usrArray;
	}
	public Boolean fitIntoSpace(ElectricityTicket t, ElectricityRequirement r, int numberNeeded, double duration, boolean mutable, ArrayList<QuantumNode> nodes)
	{
		double durB = r.getDuration();
		ArrayList<QuantumNode> rNodes = queue.findIntersectingNodes(r,nodes);
		ArrayList<QuantumNode> viableNodes = new ArrayList<QuantumNode>();
		
		Date start = rNodes.get(0).getStartTime();
		Date end   = rNodes.get(0).getEndTime();
		int tally = 0;
		for (QuantumNode q: rNodes)
		{
			if (tally < numberNeeded)
			{
			if ((q.getCapacity() >= r.getConsumption(q.getStartTime())))
			{
				tally++;
				end = q.getEndTime();
				viableNodes.add(q);
			}
			else
			{
				tally = 0;
				start = nodes.get(0).getEndTime();
				viableNodes = new ArrayList<QuantumNode>();
			}
			}
			if (tally == numberNeeded)
			{
				t.setStart(start);
				r.setStartTime(start, duration);
				t.setEnd(end);
				return addReqToNodes(r, viableNodes);
			}
		}
		return false;
	}
	public Boolean expandToFit(ElectricityTicket t, ElectricityRequirement e, ElectricityTicket oldTkt, ElectricityRequirement r, boolean mutable) {
		double durB = e.getDuration();
		double edgeB = oldTkt.getQuantisedDuration() - durB;
		double durA = r.getDuration();
		double edgeA = t.getQuantisedDuration() - durA;
		edgeA = (edgeA <= 0.) ? edgeA : 0;
		edgeB = (edgeB <= 0.) ? edgeB : 0;
		
		ElectricityRequirement reqNewA = new ElectricityRequirement(DateHelper.dPlus(t.getStart(), edgeA),
				DateHelper.dPlus(t.getEnd(), -edgeA),
				new DecimalRating(r.getPriority()), r.getProfileCode(), r.getMaxConsumption(), r.getUserID(), r.getId());
		ElectricityRequirement reqNewB = new ElectricityRequirement(DateHelper.dPlus(oldTkt.getStart(), edgeB),
				DateHelper.dPlus(oldTkt.getEnd(), -edgeB),
				new DecimalRating(e.getPriority()), e.getProfileCode(), e.getMaxConsumption(), e.getUserID(), e.getId());
		ElectricityRequirement reqA = new ElectricityRequirement(t.getStart(),t.getEnd(),
				new DecimalRating(r.getPriority()), r.getProfileCode(), r.getMaxConsumption(), r.getUserID(), r.getId());
		ElectricityRequirement reqB = new ElectricityRequirement(oldTkt.getStart(),oldTkt.getEnd(),
				new DecimalRating(e.getPriority()), e.getProfileCode(), e.getMaxConsumption(), e.getUserID(), e.getId());

		ElectricityRequirement extendDummy = new ElectricityRequirement(
				new Date((long)Math.min(t.getStart().getTime()+edgeA*QuantumNode.quanta, oldTkt.getStart().getTime()+edgeB*QuantumNode.quanta)),
				new Date((long) Math.max(t.getEnd().getTime()-edgeA*QuantumNode.quanta,oldTkt.getEnd().getTime()-edgeB*QuantumNode.quanta)),
				new DecimalRating(r.getPriority()), r.getProfileCode(), r.getMaxConsumption(), r.getUserID(), r.getId() //dummy variables
				);
		ArrayList<QuantumNode> nodes;
		if (mutable)
		{
			 nodes = queue.findIntersectingNodes(extendDummy);
		}
		else
		{
			 nodes = queue.copyIntersectingNodes(extendDummy);
		}

			removeReqFromNodes(reqB, nodes);
			removeReqFromNodes(reqA, nodes);
	
		
		
		Boolean ret = false;
		if (reqNewB.getStartTime().before(reqNewA.getStartTime()))
		{
			ret = fitIntoSpace(t, reqNewB,(int)Math.ceil(durB),durB,mutable,nodes)&&fitIntoSpace(oldTkt, reqNewA,(int)Math.ceil(durA),durA,mutable,nodes);
		}
		else
		{
			ret = fitIntoSpace(oldTkt, reqNewA,(int)Math.ceil(durA),durA,mutable,nodes)&&fitIntoSpace(t, reqNewB,(int)Math.ceil(durB),durB,mutable,nodes);
		}
		return ret; 
	}
	public Boolean extendTicket(ElectricityTicket t, ElectricityRequirement e, ElectricityTicket tktOld, ElectricityRequirement r, boolean mutable) {

		double metric = e.getDuration() - t.getQuantisedDuration();
		
		if (metric > 0) {
			return expandToFit(t, e, tktOld,r,mutable);
		}
		
		return false;
	}
	public Boolean intensifyTicket(ElectricityTicket t, ElectricityRequirement e, ElectricityRequirement match, ElectricityTicket tktOld, ElectricityRequirement r) {
		double metric = e.getMaxConsumption() - t.magnitude;
		if (metric >0)
		{
			return intensify(t,e,match,tktOld,r);
		}
		return false;
	}
	private Boolean intensify(ElectricityTicket t, ElectricityRequirement e, ElectricityRequirement match, ElectricityTicket tktOld, ElectricityRequirement r) {
		ElectricityRequirement proxy = new ElectricityRequirement(match);
		proxy.setStartTime(t.getStart(), t.getQuantisedDuration());
		ArrayList<QuantumNode> nodes = queue.findIntersectingNodes(proxy);
		removeReqFromNodes(proxy, nodes);  //removes the old requirement that has the same id as the new one
		e.setStartTime(proxy.getStartTime());
		nodes = queue.findIntersectingNodes(e);
		if(addReqToNodes(e,nodes)){ //adds the new consumption requirement
			t.setStart(e.getStartTime());
			t.setEnd(e.getEndTime());
			t.magnitude = e.getMaxConsumption();
			return true;
		}
		return false;
	
	}
	
}
