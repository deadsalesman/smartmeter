package uk.ac.imperial.smartmeter.webcomms;

/**
 * Basic data storage container used in testing and debugging.
 * Can use local or remote HLC and EDC servers which is toggleable at compile time
 * @author Ben Windo
 *
 */
public class DefaultTestClient {
	public static Boolean localOverride = true;
	public static String ipAddr = !localOverride?"155.198.117.240":LocalTestClient.ipAddr;
	public static int HLCPort = !localOverride?6666:LocalTestClient.HLCPort;
	public static int EDCPort = !localOverride?7777:LocalTestClient.EDCPort;

}
