package uk.ac.imperial.smartmeter.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.tests.allocator.AllocatorTester;
import uk.ac.imperial.smartmeter.tests.database.DBTester;

public class MasterTest {
	static Map<GenericTester, Integer> log = new HashMap<GenericTester, Integer>();;
public static void main(String[] args)
{
	addToLog(new DBTester());
	addToLog(new AllocatorTester());
	
	reportLog();
}
public static void addToLog(GenericTester g)
{
	log.put(g, g.main(null));
}
public static void reportLog()
{
	for (Entry<GenericTester, Integer> e : log.entrySet())
	{
		System.out.println(e.getKey().getClass().getName() + " has " + e.getValue() + " failures.");
	}
}
}
