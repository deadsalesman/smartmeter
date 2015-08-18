package uk.ac.imperial.smartmeter.webcomms;

import java.io.IOException;

public class HLContNode {
	private static HLCServer client;

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java HLContNode <int port number>");
			System.exit(1);
		}
		client = new HLCServer(Integer.parseInt(args[0]));
		System.out.println("High Level Server listening on " + args[0]);
		
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
