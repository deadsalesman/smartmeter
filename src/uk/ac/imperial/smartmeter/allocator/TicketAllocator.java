package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.comparators.requirementPrioComparator;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;
/**
 * Class handles initial allocation of Electricity slots to the various users of the system
 * @author Ben Windo
 * @see HLCController
 */
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
	private String userId;
	private String password;
	private boolean tryHard;
	/**
	 * Dummy Ctor for testing purposes, cannot sign tickets
	 * @param collection list of agents and their ElectricityRequirements
	 * @param d the start time of the allocation 
	 * @param reallocate allows tickets to be assigned to slightly different timeslots if they cannot
	 *                   be assigned to the original priority
	 */
	public TicketAllocator(Collection<UserAgent> collection, Date d, boolean reallocate)
	{
		this(collection, d, reallocate, "", "");
	}
	
	/**
	 * Full ctor
	 * @param collection list of agents and their ElectricityRequirements
	 * @param d the start time of the allocation 
	 * @param reallocate allows tickets to be assigned to slightly different timeslots if they cannot
	 *                   be assigned to the original priority
	 * @param id   identity of the HLCNode used for signing
	 * @param pass password of the HLCNode used for signing
	 */
	public TicketAllocator(Collection<UserAgent> collection, Date d, boolean reallocate, String id, String pass)
	{
		reqMap = new HashMap<ElectricityRequirement, ArrayList<QuantumNode>>();
		arbiter = new RescherArbiter();
		conglom = new EleGenConglomerate();
		usrArray = (ArraySet<UserAgent>) collection;
		startDate = d;
		tryHard = reallocate;
		userId = id;
		password = pass;
		
		for (UserAgent u : collection)
		{
			addUser(u);
		}

		queue = new CalendarQueue(conglom,startDate);
		populateReqMap();
	}
	/**
	 * adds a new collection of user agents to the internal data
	 * also populates the list of requirements
	 * @param collection list of agents and their ElectricityRequirements
	 */
	public void updateAgents(Collection<UserAgent> collection)
	{
		conglom = new EleGenConglomerate();
		for (UserAgent u : collection)
		{
			addUser(u);
		}
		populateReqMap();
	}
	/**
	 * for all useragents, add their requirements to a central repository of requirements
	 */
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
	/**
	 * Adds the user's generation profile to the conglomeration, and 
	 * adds the user to the local list
	 * @param user the UserAgent in question
	 */
	private void addUser(UserAgent user)
	{
		conglom.addGen(user.getGeneratedPower());
		usrArray.add(user);
	}
	/**
	 * Creates an electricity ticket based on a requirement and signs it
	 * @param req the requirement
	 * @return the ticket
	 */
	private ElectricityTicket generateTicket(ElectricityRequirement req)
	{
		ElectricityTicket tkt = new ElectricityTicket(req.getStartTime(), req.getEndTime(), req.getMaxConsumption(), req.getUserID(), req.getId());

		SignatureHelper.signTicketForNewUser(tkt, userId, password);
		System.out.println(SignatureHelper.verifyTicket(tkt));
		return tkt;
	}
	/**
	 * Finds the userAgent with the highest associated value
	 * This indicates the highest current utility and preferential treatment
	 * @param m map of userAgents to associated utilities
	 * @return the highest ranking agent
	 */
	private UserAgent findMaxAgent(Map<UserAgent,Double> m)
	{
		UserAgent maxAgt = null;
		Double max = -1.;
		for (Entry<UserAgent, Double> u : m.entrySet())
		{
			//if the user has a value greater than the current highest user
			// and they have not finished, set as new highest user
			if ((u.getValue() > max)&&(!userFinished.containsKey(u)))
			{
				maxAgt = u.getKey();
				max = u.getValue();
			}
		}
		return maxAgt;
	}
	/**
	 * Attempt to remove an electricity requirement from a set of nodes.
	 * @param req the ElectricityRequirement in question
	 * @param nodes the set of nodes to have the requirement removed.
	 * @return unconditionally true
	 */
	private Boolean removeReqFromNodes(ElectricityRequirement req, ArrayList<QuantumNode> nodes)
	{
		Boolean ret = true; //innocent until proven guilty
		for (QuantumNode n : nodes)
		{
			ret &= n.removeReq(req.getId());
		}
		return ret;
	}
	/**
	 * Attempt to add an electricity requirement to a set of nodes.
	 * 
	 * @param req the ElectricityRequirement in question
	 * @param nodes the set of nodes to have the requirement removed.
	 * @return true if successfully added
	 */
	private Boolean addReqToNodes(ElectricityRequirement req, ArrayList<QuantumNode> nodes)
	{
		Boolean viable = true; //until proved otherwise;
		for (QuantumNode n : nodes)
		{
			viable &= (n.getCapacity() >= req.getConsumption(n.getStartTime()));
		}
		
		if (viable)
		{
			for (QuantumNode n : nodes)
			{
				n.addReq(req);
			}
		}
		return viable;
	}
	/**
	 * Attempts to create an electricity ticket from a requirement for a certain user
	 * @param u the user in question
	 * @param req the requirement in question
	 * @return the ticket
	 */
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
	/**
	 * Attempts to create an electricity ticket from a requirement for a certain user
	 * The requirement has been shifted in time to attempt a better fit
	 * @param u the user in question
	 * @param req the requirement in question
	 * @param proxy the time shifted requirement
	 * @return the ticket
	 */
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
	/**
	 * Attempts to create a ticket for an ElectricityRequirement
	 * If {@link TicketAllocator#tryHard} is set, shifts the requirement in time for a better fit
	 * 
	 * @param u the user that owns the requirement
	 * @param req the requirement in question
	 * @return success?
	 */
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
	
	/**
	 * finds the quantity as a number between 0..1 that a electricityRequirement makes 
	 * up of the overall electricity demand of a user
	 * where 1 indicates it is the only requirement
	 * 
	 * @param offset the number of requirements that have already been considered
	 * @param reqs the array of requirements
	 * @return the value between 0..1
	 */
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
	/**
	 * Attempts to calculate tickets for the currently entered user agents
	 * @return the user agents with their tickets added
	 */
	public ArraySet<UserAgent> calculateTickets()
	{
		//Evaluates the weightings of the existing users
		rankings = arbiter.getWeighting(usrArray);
		indexes = new HashMap<UserAgent, Integer>();
		userFinished = new HashMap<UserAgent, Boolean>();
		
		Boolean finished = false;
		
		for (UserAgent u : usrArray)
		{
			//Sort the users by ranks
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
				if ((newRank ==0)||(Double.isNaN(newRank)))
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
	/**
	 * Attempts create a ticket for the electricity requirement given a small set of nodes
	 * @param t   The ticket to be modified
	 * @param req The requirement in question
	 * @param numberNeeded The number of nodes needed to satisfy the requirement
	 * @param duration The duration of the electricity requirement in non-integer nodes
	 * @param nodes The available nodes to attempt to move the requirement to
	 * @return Success?
	 */
	public Boolean fitIntoSpace(ElectricityTicket t, ElectricityRequirement req, int numberNeeded, double duration, ArrayList<QuantumNode> nodes)
	{
		double durB = req.getDuration();
		ArrayList<QuantumNode> rNodes = queue.findIntersectingNodes(req,nodes);
		ArrayList<QuantumNode> viableNodes = new ArrayList<QuantumNode>();
		
		Date start = rNodes.get(0).getStartTime();
		Date end   = rNodes.get(0).getEndTime();
		int tally = 0;
		for (QuantumNode q: rNodes)
		{
			if (tally < numberNeeded)
			{
			if ((q.getCapacity() >= req.getConsumption(q.getStartTime())))
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
				req.setStartTime(start, duration);
				t.setEnd(end);
				return addReqToNodes(req, viableNodes);
			}
		}
		return false;
	}
	/**
	 * Attempts to expand a ticket to allow a longer ElectricityRequirement to fit in the same space.
	 * 
	 * @param newTkt
	 * @param oldReq
	 * @param oldTkt
	 * @param newReq
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean expandToFit(ElectricityTicket newTkt, ElectricityRequirement oldReq, ElectricityTicket oldTkt, ElectricityRequirement newReq, boolean mutable) {
		double durB = oldReq.getDuration();
		double edgeB = oldTkt.getQuantisedDuration() - durB;
		double durA = newReq.getDuration();
		double edgeA = newTkt.getQuantisedDuration() - durA;
		edgeA = (edgeA <= 0.) ? edgeA : 0;
		edgeB = (edgeB <= 0.) ? edgeB : 0;
		
		ElectricityRequirement reqNewA = new ElectricityRequirement(DateHelper.dPlus(newTkt.getStart(), edgeA),
				DateHelper.dPlus(newTkt.getEnd(), -edgeA),
				new DecimalRating(newReq.getPriority()), newReq.getProfileCode(), newReq.getMaxConsumption(), newReq.getUserID(), newReq.getId());
		ElectricityRequirement reqNewB = new ElectricityRequirement(DateHelper.dPlus(oldTkt.getStart(), edgeB),
				DateHelper.dPlus(oldTkt.getEnd(), -edgeB),
				new DecimalRating(oldReq.getPriority()), oldReq.getProfileCode(), oldReq.getMaxConsumption(), oldReq.getUserID(), oldReq.getId());
		ElectricityRequirement reqA = new ElectricityRequirement(newTkt.getStart(),newTkt.getEnd(),
				new DecimalRating(newReq.getPriority()), newReq.getProfileCode(), newReq.getMaxConsumption(), newReq.getUserID(), newReq.getId());
		ElectricityRequirement reqB = new ElectricityRequirement(oldTkt.getStart(),oldTkt.getEnd(),
				new DecimalRating(oldReq.getPriority()), oldReq.getProfileCode(), oldReq.getMaxConsumption(), oldReq.getUserID(), oldReq.getId());

		ElectricityRequirement extendDummy = new ElectricityRequirement(
				new Date((long)Math.min(newTkt.getStart().getTime()+edgeA*QuantumNode.quanta, oldTkt.getStart().getTime()+edgeB*QuantumNode.quanta)),
				new Date((long) Math.max(newTkt.getEnd().getTime()-edgeA*QuantumNode.quanta,oldTkt.getEnd().getTime()-edgeB*QuantumNode.quanta)),
				new DecimalRating(newReq.getPriority()), newReq.getProfileCode(), newReq.getMaxConsumption(), newReq.getUserID(), newReq.getId() //dummy variables
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
			ret = fitIntoSpace(newTkt, reqNewB,(int)Math.ceil(durB),durB,nodes)&&fitIntoSpace(oldTkt, reqNewA,(int)Math.ceil(durA),durA,nodes);
		}
		else
		{
			ret = fitIntoSpace(oldTkt, reqNewA,(int)Math.ceil(durA),durA,nodes)&&fitIntoSpace(newTkt, reqNewB,(int)Math.ceil(durB),durB,nodes);
		}
		return ret; 
	}
	/**
	 * Attempts to extend a ticket if the ticket duration is less than the requirement duration
	 * @param t
	 * @param e
	 * @param tktOld
	 * @param r
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean extendTicket(ElectricityTicket t, ElectricityRequirement e, ElectricityTicket tktOld, ElectricityRequirement r, boolean mutable) {

		double metric = e.getDuration() - t.getQuantisedDuration();
		
		if (metric > 0) {
			return expandToFit(t, e, tktOld,r,mutable);
		}
		
		return false;
	}
	/**
	 * Attempts to intensify a ticket if the ticket amplitude is less than the requirement amplitude
	 * @param t
	 * @param e
	 * @param tktOld
	 * @param r
	 * @param mutable
	 * @return
	 */
	public Boolean intensifyTicket(ElectricityTicket t, ElectricityRequirement e, ElectricityTicket tktOld, ElectricityRequirement r, boolean mutable) {
		double metric = e.getMaxConsumption() - t.magnitude;
		if (metric >0)
		{
			return intensify(t,e,tktOld,r,mutable);
		}
		return false;
	}
	/**
	 * Attempt to intensify a ticket to allow a higher amplitude requirement to fit in that slot
	 * @param t
	 * @param e
	 * @param tktOld
	 * @param r
	 * @param mutable
	 * @return
	 */
	private Boolean intensify(ElectricityTicket t, ElectricityRequirement e, ElectricityTicket tktOld, ElectricityRequirement r, boolean mutable) {

		ElectricityRequirement reqA = new ElectricityRequirement(t.getStart(),t.getEnd(),
				new DecimalRating(r.getPriority()), r.getProfileCode(), r.getMaxConsumption(), r.getUserID(), r.getId());
		ElectricityRequirement reqB = new ElectricityRequirement(tktOld.getStart(),tktOld.getEnd(),
				new DecimalRating(e.getPriority()), e.getProfileCode(), e.getMaxConsumption(), e.getUserID(), e.getId());
		
		ArrayList<QuantumNode> nodesA;
		ArrayList<QuantumNode> nodesB;
		
		if (mutable)

		{
		nodesA = queue.findIntersectingNodes(reqA);
		nodesB = queue.copyIntersectingNodes(reqB);
		}

		else
		{
		nodesA = queue.copyIntersectingNodes(reqA);
		nodesB = queue.copyIntersectingNodes(reqB);
		}

        removeReqFromNodes(reqB, nodesB);
		removeReqFromNodes(reqA, nodesA);
		
		Boolean successA = addReqToNodes(reqA,nodesB);
		Boolean successB = addReqToNodes(reqB,nodesA);
		
		if(successA){ //adds the new consumption requirement
			t.magnitude = e.getMaxConsumption();
		}
		if(successB){
			tktOld.magnitude = r.getMaxConsumption();
		}
		return successA&&successB;
	
	}
	
}
