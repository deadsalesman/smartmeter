package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;

public class HLContNode {
	private static HLCServer client;

	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.err.println("Usage: java HLContNode <int port number> <true?false verbose>");
			System.exit(1);
		}
		client = new HLCServer(Integer.parseInt(args[0]), Boolean.parseBoolean(args[1]));
		System.out.println("High Level Server listening on " + args[0]);
		while(client.listen()){}
	}
}
