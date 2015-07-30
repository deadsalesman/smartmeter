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
        client = new LCClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
        ArrayList<String> test = new ArrayList<String>();
        String t = UUID.randomUUID().toString();
        test.add("ADD,True,1,"+t);
        test.add("GETD,"+t);
        //client.setState(deviceId, val)
        client.connectEDC(test);
	}
}
