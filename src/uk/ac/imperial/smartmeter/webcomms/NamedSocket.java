package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;

public class NamedSocket {
	public String name;
	public InetSocketAddress socket;
	public NamedSocket(String key, InetSocketAddress value) {
		name = key;
		socket = value;
	}

}
