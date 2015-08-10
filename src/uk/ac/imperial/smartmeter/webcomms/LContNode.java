package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LContNode {
	private static LCClient client;

	public static void main(String[] args) throws IOException {

		if (args.length != 6) {
			System.err.println("Usage: java LContNode <host name> <port number> <host name> <port number> <username> <password>");
			System.exit(1);
		}
		client = new LCClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), args[4],args[5]); // TODO

	}

	public static ArrayList<String> connect(ArrayList<String> input) {
		try {
			return client.connectEDC(input);
		} catch (IOException e) {
			return null;
		}
	}
	public static String getUserId()
	{
		return client.getId();
	}
	public static Boolean addDevice(boolean b, int i, String id) {
		return client.addDevice(b, i, id);
	}

	public static Boolean getState(String id) {
		return client.getState(id);

	}

	public static Boolean setState(String id, Boolean val) {
		return client.setState(id, val);
	}

	public static Boolean removeDevice(String id) {
		return client.removeDevice(id);
	}
	public static Boolean registerUser(Double worth, Double generation, Double economic, int port)
	{
		return client.registerUser(worth, generation, economic, port);
	}
	public static Boolean setRequirement(ElectricityRequirement req) {
		return client.setRequirement(req);
		
	}

	public static ArraySet<ElectricityTicket> getTickets() {
		return client.getTickets();
		
	}

	public static boolean setGeneration(ElectricityGeneration i) {
		return client.setGeneration(i);
	}
}
