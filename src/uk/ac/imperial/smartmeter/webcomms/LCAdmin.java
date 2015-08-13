package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map.Entry;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCAdmin implements Runnable{

	Thread t;
	private int interval;
	private Double timeSinceLastTickets=0.;
	private Double timeSinceLastBulletin=0.;
	private Double timeSinceLastNegotiation=0.;

	private Double reasonableBulletinTime = Math.pow(10., 7.5);
	private Double reasonableTicketTime = Math.pow(10., 7.5);
	private Double reasonableNegotiationTime = Math.pow(10.,7.6);
	private Double pollingTime = Math.pow(10., 6);
	
	private Bulletin bulletin;
	public LCClient client;
	private int ownPort;
	private Boolean active = true;
	public LCAdmin(LCClient lc, int port)
	{
		client = lc;
		bulletin = new Bulletin();
		ownPort = port;
	}
	public void activate()
	{
		active = true;
	}
	public void deactivate()
	{
		active = false;
	}
	private Boolean requestBulletin() {
		HashMap<String, InetSocketAddress> x = client.getPeers();
		if (x!= null) {
		for (Entry<String, InetSocketAddress> e : x.entrySet())
		{
			
			if (e.getValue()!=null)
			{
				if (!e.getKey().equals(client.getId())) 
				{
				bulletin.add(new NamedSocket(e.getKey(),e.getValue()));
			}
			}
		}
		}
		bulletin.sociallyAwah = true;
		return false;
	}
	public void start() {
		System.out.println("Client admin active.");
		if (t == null)
	      {
	         t = new Thread (this, (client.handler.getId()+"admin"));
	         t.start ();
	      }
	}
	@Override
	public void run() {

		int attempts = 3;
		
	
		while(active)
		{
			if (interval > pollingTime)
			{
				interval = 0;

				timeSinceLastTickets += pollingTime;
				timeSinceLastBulletin += pollingTime;
				timeSinceLastNegotiation += pollingTime;
				
			if (client.queryUnsatisfiedReqs()&&(timeSinceLastTickets>reasonableTicketTime))
			{
				//attempt to get tickets from the central server
				client.getTickets();
				timeSinceLastTickets = 0.;
			}
			if (timeSinceLastBulletin > reasonableBulletinTime)
			{
				requestBulletin();
				timeSinceLastBulletin =0.;
			}
			if ((bulletin.sociallyAwah)
					&&(timeSinceLastNegotiation > reasonableNegotiationTime)
					&&(client.queryUnhappyTickets()))
			{

				timeSinceLastNegotiation =0.;
				//attempt to exchange tickets with other clients
				for (ElectricityTicket t : client.getUnhappyTickets())
				{
					int i = 0;
					Boolean successfulTrade = false;
					while (i < attempts && !successfulTrade)
					{
						i++;
						InetSocketAddress addr = bulletin.getNextAddress();
						String location = addr.getHostName();
						int port = addr.getPort();
						client.registerClient(location, port,ownPort);
						ElectricityRequirement req = client.handler.findMatchingRequirement(t);
					ArraySet<ElectricityTicket> tkts = client.queryCompeting(location, port, req);
					if (tkts != null)
					{
					for (ElectricityTicket e : tkts)
					{
						if(!successfulTrade)
						{
					successfulTrade = client.offer(location, port, e,t);
						}
					}
					}
					}
				}
				}
			
			}
			else
			{
				interval ++;
			}
		}
	}
	public void stop() {
		active = false;
	}

}
