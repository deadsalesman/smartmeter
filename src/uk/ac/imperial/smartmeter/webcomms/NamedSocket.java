package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;

public class NamedSocket {
	public String name;
	public InetSocketAddress socket;
	public String pubkey;
	public NamedSocket(String key, InetSocketAddress value) {
		name = key;
		socket = value;
	}
	public NamedSocket(String key, InetSocketAddress value, String pub) {
		this(key,value);
		pubkey = pub;
	}

}
