package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class TicketTestHelper {
	
	public static String user1 = "Vincent Vega";
	public static String user2 = "Jules Winnfield";
	public static String user3 = "Mia Wallace";
	public static String user4 = "Butch Coolidge";
	public static String user5 = "Winston Wolfe";
	public static String user6 = "Marsellus Wallace";
	

	public static ArraySet<UserAgent> array = new ArraySet<UserAgent>();
	
	public TicketTestHelper()
	{
		
	}
	public static ElectricityRequirement bindRequirement(UserAgent u, Double start, Double end, Integer prio, int amplitude)
	{
		return bindRequirement(u,start,end,prio,new Double(amplitude));
	}
	public static ElectricityRequirement bindRequirement(LCClient lc, Double start, Double end, Integer prio, Double amplitude)
	{
		ElectricityRequirement ele = new ElectricityRequirement(
				DateHelper.os(start), 
				DateHelper.os(end), 
				new DecimalRating(prio), 
				0, 
				amplitude, 
				lc.getId());
		
		lc.setRequirement(ele);
		return ele;
	}
	public static ElectricityRequirement bindRequirement(UserAgent u, Double start, Double end, Integer prio, Double amplitude)
	{
		ElectricityRequirement ele = new ElectricityRequirement(
				DateHelper.os(start), 
				DateHelper.os(end), 
				new DecimalRating(prio), 
				0, 
				amplitude, 
				u.getId());
		
		u.addReq(ele);
		return ele;
	}
	public static void printTickets(Map<UserAgent, ArraySet<ElectricityTicket>> m)
	{
		for (Entry<UserAgent, ArraySet<ElectricityTicket>> e : m.entrySet())
		{
			for (ElectricityTicket t : e.getValue())
			{
				System.out.println(e.getKey().getName() + " " + t.magnitude +" " +  t.getStart().getTime() + " " + t.getEnd().getTime());
			}
		}
	}
	public static long normaliseTime(Date d, Date ref)
	{
		return (d.getTime()-ref.getTime())/QuantumNode.quanta;
	}
	public static void printTickets(ArraySet<UserAgent> x, Date d)
	{
		for (UserAgent u : x)
		{
			for (ElectricityTicket t : u.getReqTktMap().values())
			{
				if (t!= null)
				{
					System.out.println(u.getName() + ", " + t.magnitude +", " +  normaliseTime(t.getStart(),d) + ", " + normaliseTime(t.getEnd(),d));
				}
			}
		}
	}
	public static int countTickets(ArraySet<UserAgent> x)
	{
		int ret = 0;
		for (UserAgent u : x)
		{
			ret += u.countTkts();
		}
		return ret;
	}
}
