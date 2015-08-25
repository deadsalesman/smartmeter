package uk.ac.imperial.smartmeter.tests;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.tests.allocator.AllocatorTester;
import uk.ac.imperial.smartmeter.tests.autonomous.AutonTester;
import uk.ac.imperial.smartmeter.tests.database.DBTester;
import uk.ac.imperial.smartmeter.tests.network.NetworkTester;

public class MasterTest {
	static Map<GenericTester, Integer> log = new HashMap<GenericTester, Integer>();;
public static void main(String[] args)
{

	DateHelper.initialise();
    Security.addProvider(new BouncyCastleProvider());

	addToLog(new NetworkTester());
	addToLog(new DBTester());
	addToLog(new AllocatorTester());

	addToLog(new AutonTester());
	
	reportLog();
}
public static void addToLog(GenericTester g)
{
	log.put(g, g.main());
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
	System.exit(0);
}
}
