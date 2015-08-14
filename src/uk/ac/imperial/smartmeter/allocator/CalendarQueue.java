package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.res.EleGenConglomerate;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;

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
 private void initialiseCalendar()
 {
	 for (int i = 0; i < daysInCalendar; i++)
	 {
		 calendar.add(new DayNode(startTime, i, conglom));
	 }
 }
 public ArrayList<QuantumNode> findIntersectingNodes(ElectricityRequirement req, ArrayList<QuantumNode> nodes)
 {
	 ArrayList<QuantumNode> ret = new ArrayList<QuantumNode>();
	 for (QuantumNode q : nodes)
	 {
		 if (q.getEndTime().before(req.getStartTime())){
				
			}
			else
			{
				if (q.getStartTime().after(req.getEndTime()))
				{
			      //break; //unsafe optimisation given the state of nodes is unknown and ordering cannot be relied upon.
				}
				else
				{
					ret.add(q);
				}
			}
	 }
	 return ret;
 }
 public ArrayList<QuantumNode> findIntersectingNodes(ElectricityRequirement req)
 {
	 ArrayList<QuantumNode> ret = new ArrayList<QuantumNode>();
	for (DayNode d : calendar)
	{
		for (QuantumNode q : d.nodes)
		{
			if (q.getEndTime().before(req.getStartTime())){
			
			}
			else
			{
				if (q.getStartTime().after(req.getEndTime()))
				{
			      break;
				}
				else
				{
					ret.add(q);
				}
			}
		}
	}
	 return ret;
 }
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
 public DayNode push(DayNode d)
 {
	 DayNode pers;
	 DayNode temp = d;
	 
	 
	 for (int i = daysInCalendar-1; i >= 0; i--)
	 {
		 pers = calendar.get(i);
		 calendar.set(i, temp);
		 temp = pers;
	 }
	 
	 
	 return temp;
 }
 public DayNode increment()
 {
	 return push(new DayNode(conglom,calendar.get(daysInCalendar-1).getEndTime()));
 }

}
