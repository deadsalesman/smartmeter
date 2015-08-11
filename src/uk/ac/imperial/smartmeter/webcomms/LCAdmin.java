package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.HashMap;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;

public class LCAdmin implements Runnable{

	Thread t;
	private int interval;
	private Double timeSinceLastTickets=0.;
	private Double timeSinceLastBulletin=0.;
	private Bulletin bulletin;
	public LCClient client;
	private Boolean active = true;
	public LCAdmin(LCClient lc)
	{
		client = lc;
		bulletin = new Bulletin();
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
		if (x!=null)
		{
			bulletin.set(x);
			return true;
		}
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

		Double reasonableBulletinTime = Math.pow(10., 8.5);
		Double reasonableTicketTime = Math.pow(10., 8.5);
		Double pollingTime = Math.pow(10., 7);
		int attempts = 3;
		
	
		while(active)
		{
			if (interval > pollingTime)
			{
				interval = 0;

				timeSinceLastTickets += pollingTime;
				timeSinceLastBulletin += pollingTime;
				
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
			if (client.queryUnhappyTickets())
			{
				//attempt to exchange tickets with other clients
				for (ElectricityTicket t : client.getUnhappyTickets())
				{
					int i = 0;
					Boolean successfulTrade = false;
					while (i < attempts && !successfulTrade)
					{
						InetSocketAddress addr = bulletin.getNextAddress();
						String location = addr.getHostName();
						int port = addr.getPort();
					ArraySet<ElectricityTicket> tkts = client.queryCompeting(location, port, client.handler.findMatchingRequirement(t));
					for (ElectricityTicket e : tkts)
					{
					successfulTrade = client.offer(location, port, t, e);
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
