package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LContNode{
	private static LCClient client;
	
	public static void main(String[] args) throws IOException {
	    
        if (args.length != 4) {
            System.err.println(
                "Usage: java LContNode <host name> <port number> <host name> <port number>");
            System.exit(1);
        }
        client = new LCClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), UUID.randomUUID().toString()); //TODO

	}
	public static ArrayList<String> connect(ArrayList<String> input)
	{
		try {
			return client.connectEDC(input);
		} catch (IOException e) {
			return null;
		}
	}
	public static boolean addDevice(boolean b, int i, String id) {
		return client.addDevice(b, i, id);
	}
	public static boolean getState(String id) {
		return client.getState(id);
	}
}
