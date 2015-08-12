package uk.ac.imperial.smartmeter.webcomms;

public class DefaultTestClient {
	public static Boolean localOverride = true;
	public static String ipAddr = !localOverride?"155.198.117.240":LocalTestClient.ipAddr;
	public static int HLCPort = !localOverride?6666:LocalTestClient.HLCPort;
	public static int EDCPort = !localOverride?7777:LocalTestClient.EDCPort;

}
