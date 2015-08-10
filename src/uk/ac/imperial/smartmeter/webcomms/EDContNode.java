package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;

public class EDContNode {
	private static EDCServer client;
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java EDContNode <port number>");
			System.exit(1);
		}
		client = new EDCServer(Integer.parseInt(args[0]));
		System.out.println("Device Server listening on : " + args[0]);
		while(client.listen()){}
	}
	
}
