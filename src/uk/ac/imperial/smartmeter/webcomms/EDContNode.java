package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;
/**
 * Basic class to instantiate an EDCServer 
 * On reflection this entire thing is pointless and a main in EDCServer would be superior in every conceivable way.
 * @deprecated
 * @author Ben Windo
 *
 */
public class EDContNode {
	private static EDCServer client;
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java EDContNode <int port number>");
			System.exit(1);
		}

		System.setProperty("java.rmi.server.hostname", DefaultTestClient.ipAddr); 
		client = new EDCServer(Integer.parseInt(args[0]));

		System.out.println("Device Server listening on : " + args[0]);
		while(true)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
