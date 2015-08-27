package uk.ac.imperial.smartmeter.webcomms;

import java.net.InetSocketAddress;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Twople;

public class LCAdmin implements Runnable{

	Thread t;
	private int interval;
	private Double timeSinceLastTickets=0.;
	private Double timeSinceLastBulletin=0.;
	private Double timeSinceLastNegotiation=0.;
	private Double timeSinceLastReqCheck=0.;


	private Double reasonableReqCheckTime;
	private Double reasonableBulletinTime;
	private Double reasonableTicketTime;
	private Double reasonableNegotiationTime;
	private Double pollingTime = Math.pow(10., 8);
	
	private String pubkey;
	private Bulletin bulletin;
	public LCClient client;
	private int ownPort;
	private Boolean active = true;
	

	private ArraySet<ElectricityRequirement> newReqs;
	private ArraySet<ElectricityRequirement> currentReqs;
	private ArraySet<ElectricityRequirement> deadReqs;
	
	public LCAdmin(LCClient lc, int port, String pubKey)
	{
		pubkey = pubKey;
		client = lc;
		bulletin = new Bulletin();
		ownPort = port;
		Random rn = new Random();
		reasonableReqCheckTime = Math.pow(10., 7.8 + rn.nextInt(50)/100);
		reasonableBulletinTime = Math.pow(10., 8.8 + rn.nextInt(50)/100);
		reasonableTicketTime = Math.pow(10., 8.8 + rn.nextInt(50)/100);
		reasonableNegotiationTime = Math.pow(10.,8.9 + rn.nextInt(50)/100);

		newReqs = new ArraySet<ElectricityRequirement>();
		currentReqs = new ArraySet<ElectricityRequirement>();
		deadReqs = new ArraySet<ElectricityRequirement>();
		
		updateRequirementClasses(client.handler.getReqs());
	}
	public Integer getRequirementCategory(ElectricityRequirement e)
	{
		Date d = new Date();
		Integer ret = -1;
		if (e.getEndTime().compareTo(d) <= 0)
		{
			ret = 2;
		}
		else if (e.getStartTime().compareTo(d) >= 0)
		{
			ret = 0;
		}
		else
		{
			ret = 1;
		}
		
		return ret;
	}
	public void updateRequirementClasses(ArraySet<ElectricityRequirement> reqs)
	{
		for (ElectricityRequirement r : reqs)
		{
			switch(getRequirementCategory(r))
			{
			case -1:
				System.out.println("The requirement category algorithm is broken in LCAdmin");
				break;
			case 0:
				newReqs.add(r);
				break;
			case 1:
				currentReqs.add(r);
				break;
			case 2:
				deadReqs.add(r);
				break;
			}
		}

	}
	public void modifyRequirementClasses()
	{
		try{
		for (ElectricityRequirement r: currentReqs)
		{
			if (getRequirementCategory(r)==2)
			{
				currentReqs.remove(r);
				deadReqs.add(r);
				client.setState(r.getDevice().getId(), false);
			}
		}
		for (ElectricityRequirement r: newReqs)
		{
			if (getRequirementCategory(r)==1)
			{
				newReqs.remove(r);
				currentReqs.add(r);
				checkTickets(r);
			}
		}
		}
		catch(ConcurrentModificationException e)
		{
			
		}
	}
	private void checkTickets(ElectricityRequirement r) {
		ElectricityTicket q = client.handler.findMatchingTicket(r);
		if (q!=null)
		{
			q.setActive(true);
			System.out.println(client.setState(r.getDevice().getId(), true));
		}
		
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
		HashMap<String, Twople<String,InetSocketAddress>> x = client.getAddresses();
		if (x!= null) {
		for (Entry<String, Twople<String,InetSocketAddress>> e : x.entrySet())
		{
			
			if (e.getValue()!=null)
			{
				if (!e.getKey().equals(client.getId())) 
				{
				bulletin.add(new NamedSocket(e.getKey(),e.getValue().right,e.getValue().left));
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
				timeSinceLastReqCheck += pollingTime;
			if (timeSinceLastReqCheck > reasonableReqCheckTime)
			{
				updateRequirementClasses(client.newReqs);
				client.newReqs = new ArraySet<ElectricityRequirement>();
				modifyRequirementClasses();
			}
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
						if (addr!=null)
						{
						String location = addr.getHostName();
						int port = addr.getPort();
						client.registerClient(location, port,ownPort, pubkey);
						ElectricityRequirement req = client.handler.findMatchingRequirement(t);
					ArraySet<ElectricityTicket> tkts = client.queryCompeting(location, port, req);
					if (tkts != null)
					{
					for (ElectricityTicket e : tkts)
					{
						if(!successfulTrade)
						{
							if (LCServer.calcUtilityNoExtension(e, req) > LCServer.calcUtilityNoExtension(t, req))
							{
							//System.out.println(LCServer.calcUtilityNoExtension(e, req)+" : " + LCServer.calcUtilityNoExtension(t, req));

								successfulTrade = client.offer(location, port, e,t).success;
								if (successfulTrade){}//System.out.println(e.toString()+" " + t.toString());}
							}
						}
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
