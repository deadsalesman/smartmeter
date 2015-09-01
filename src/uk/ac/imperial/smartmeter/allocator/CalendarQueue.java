package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

/**
 * 
 * @author Ben Windo
 * <p>
 *  Stores CalendarQueue#daysInCalendar
 *  instances of DayNode for the TicketAllocator
 * </p>
 * @see DayNode
 * @see TicketAllocator
 */
public class CalendarQueue {
 private ArrayList<DayNode> calendar;
 public static final int  daysInCalendar = 5; 
 private EleGenConglomerate conglom;
 private Date startTime;
 public int getCalendarSize()
 {
	 return calendar.size();
 }
 public int getDayNodeSize()
 {
	 return calendar.get(0).getSize();
 }
 /**
  *  generates and populates the DayNodes.
	 QuantumNode capacities are set based on the predicted availability at the 
	 time, given by the EleGenConglomerate conglom
  */
 private void initialiseCalendar()
 {
	 
	 for (int i = 0; i < daysInCalendar; i++)
	 {
		 calendar.add(new DayNode(startTime, i, conglom));
	 }
 }
 /**
  * Returns a list of all QuantumNodes in nodes whose start and end times intersect with those of the ElectricityRequirement
  * @param req the ElectricityRequirement in question
  * @param nodes the possible QuantumNodes that may or may not be active at the same time as req
  * @return a list of all QuantumNodes in nodes that exist in the time period specified by the ElectricityRequirement req
  */
 public ArrayList<QuantumNode> findIntersectingNodes(ElectricityRequirement req, ArrayList<QuantumNode> nodes)
 {
	 
	 ArrayList<QuantumNode> ret = new ArrayList<QuantumNode>();
	 for (QuantumNode q : nodes)
	 {
		 //If the node finishes before the req starts, it does not intersect
		 if (q.getEndTime().before(req.getStartTime())){
				
			}
			else
			{
				//If the node does not start after the req ends, it intersects
				if (!q.getStartTime().after(req.getEndTime()))
				{
					ret.add(q);
				}
			}
	 }
	 return ret;
 }
 /**
 * Returns a list of all QuantumNodes in nodes whose start and end times intersect with those of the CalendarQueue#calendar field
  * @param req the ElectricityRequirement in question
  * @return a list of all QuantumNodes in nodes that exist in the time period specified by the ElectricityRequirement req
  */
 public ArrayList<QuantumNode> findIntersectingNodes(ElectricityRequirement req)
 {
	 ArrayList<QuantumNode> ret = new ArrayList<QuantumNode>();
	 for (DayNode d : calendar)
	 {
		 ret.addAll(findIntersectingNodes(req, d.nodes));
	 }
	 return ret;
 }
 /**
  * Returns a deep copy of all of the nodes found to intersect a certain ElectricityRequirement
  * This is useful when actions need to be taken that should not change the actual values of the underlying QuantumNodes
  * 
  * @param req the ElectricityRequirement in question
  * @return a copied list of all QuantumNodes in nodes that exist in the time period specified by the ElectricityRequirement req
  */
 public ArrayList<QuantumNode> copyIntersectingNodes(ElectricityRequirement req)
 {
	 ArrayList<QuantumNode> originalNodes = findIntersectingNodes(req);
	 ArrayList<QuantumNode> ret = new ArrayList<QuantumNode>();
	 
	 for (QuantumNode q : originalNodes)
	 {
		 ret.add(new QuantumNode(q));
	 }
	 return ret;
 }
 public boolean findDayNode(DayNode d)
 {
	 return calendar.contains(d);
 }
 public CalendarQueue(EleGenConglomerate e, Date d)
 {
	 calendar = new ArrayList<DayNode>(daysInCalendar);
	 startTime = d;
	 conglom = e;
	 initialiseCalendar();
 }
 /**
  * @deprecated
  * @param e
  */
 public void updateCapacities(EleGenConglomerate e)
 {
	 for (DayNode d : calendar)
	 {
		 for (QuantumNode q : d.nodes)
		 {
			 //TODO: Make this work. 
			 q.removeReq(0);
		 }
	 }
 }
 /**
  * Adds a DayNode to the internal calendar, replacing the most recent one
  * @param d the DayNode to be added
  * @return the DayNode removed from the internal calendar
  */
 public DayNode push(DayNode d)
 {
	 DayNode pers;
	 DayNode temp = d;
	 
	 //Move all nodes to be more recent, placing the new node as the furthest in the future
	 for (int i = daysInCalendar-1; i >= 0; i--)
	 {
		 pers = calendar.get(i);
		 calendar.set(i, temp);
		 temp = pers;
	 }
	 
	 
	 return temp;
 }
 /**
  * Adds a new DayNode to the internal calendar which is the natural extension of the calendar
  * @return the removed node
  */
 public DayNode increment()
 {
	 return push(new DayNode(conglom,calendar.get(daysInCalendar-1).getEndTime()));
 }

}
