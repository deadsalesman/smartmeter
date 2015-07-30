package uk.ac.imperial.smartmeter.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.tests.allocator.AllocatorTester;
import uk.ac.imperial.smartmeter.tests.database.DBTester;
import uk.ac.imperial.smartmeter.tests.network.NetworkTester;

public class MasterTest {
	static Map<GenericTester, Integer> log = new HashMap<GenericTester, Integer>();;
public static void main(String[] args)
{
	addToLog(new DBTester());
	addToLog(new AllocatorTester());
	addToLog(new NetworkTester());
	
	reportLog();
}
public static void addToLog(GenericTester g)
{
	log.put(g, g.main(null));
}
public static void reportLog()
{
	int total = 0;
	for (Entry<GenericTester, Integer> e : log.entrySet())
	{
		System.out.println(e.getKey().getClass().getName() + " has " + e.getValue() + " failures.");
		total += e.getValue();
	}
	System.out.println("There are " + total +  " failed tests overall.");
}
}
