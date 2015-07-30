package uk.ac.imperial.smartmeter.tests.allocator;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.allocator.UserAgent;
import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.User;

public class TicketTestHelper {
	
	public static User user1 = new User("Vincent Vega");
	public static User user2 = new User("Jules Winnfield");
	public static User user3 = new User("Mia Wallace");
	public static User user4 = new User("Butch Coolidge");
	public static User user5 = new User("Winston Wolfe");
	public static User user6 = new User("Marsellus Wallace");
	

	public static ArraySet<UserAgent> array = new ArraySet<UserAgent>();
	
	public TicketTestHelper()
	{
		
	}
	public static ElectricityRequirement bindRequirement(UserAgent u, Double start, Double end, Integer prio, int amplitude)
	{
		return bindRequirement(u,start,end,prio,new Double(amplitude));
	}
	public static ElectricityRequirement bindRequirement(UserAgent u, Double start, Double end, Integer prio, Double amplitude)
	{
		ElectricityRequirement ele = new ElectricityRequirement(
				DateHelper.os(start), 
				DateHelper.os(end), 
				new DecimalRating(prio), 
				1, 
				amplitude, 
				u.getUser().getId());
		
		u.addReq(ele);
		return ele;
	}
	public static void printTickets(Map<UserAgent, ArraySet<ElectricityTicket>> m)
	{
		for (Entry<UserAgent, ArraySet<ElectricityTicket>> e : m.entrySet())
		{
			for (ElectricityTicket t : e.getValue())
			{
				System.out.println(e.getKey().getUser().getName() + " " + t.magnitude +" " +  t.start.getTime() + " " + t.end.getTime());
			}
		}
	}
	public static long normaliseTime(Date d, Date ref)
	{
		return (d.getTime()-ref.getTime())/QuantumNode.quanta;
	}
	public static void printTickets(Map<UserAgent, ArraySet<ElectricityTicket>> m, Date d)
	{
		for (Entry<UserAgent, ArraySet<ElectricityTicket>> e : m.entrySet())
		{
			for (ElectricityTicket t : e.getValue())
			{
				System.out.println(e.getKey().getUser().getName() + ", " + t.magnitude +", " +  normaliseTime(t.start,d) + ", " + normaliseTime(t.end,d));
			}
		}
	}
	public static int countTickets(Map<UserAgent, ArraySet<ElectricityTicket>> m)
	{
		int ret = 0;
		for (Entry<UserAgent, ArraySet<ElectricityTicket>> e : m.entrySet())
		{
			ret += e.getValue().getSize();
		}
		return ret;
	}
}
