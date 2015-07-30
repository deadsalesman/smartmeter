package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;

import uk.ac.imperial.smartmeter.impl.HLCHandler;

public class HLContNode {
	private static HLCServer client;
	private static HLCHandler handler;

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java HLContNode <port number>");
			System.exit(1);
		}
		client = new HLCServer(Integer.parseInt(args[0]));
		client.listen();
		handler.setRequirement(null);
	}
}
