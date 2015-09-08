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
import uk.ac.imperial.smartmeter.res.Triple;

/**
 * Class that handles autonomous control of the LocalController.
 * Issues requests which are responded to by remote servers via the {@link LCClient}
 * @author bwindo
 * @see LCClient
 * @see LCServer
 */
public class LCAdmin implements Runnable{

	Thread t;
	private int interval;
	private Double timeSinceLastTickets=0.;
	private Double timeSinceLastBulletin=0.;
	private Double timeSinceLastNegotiation=0.;
	private Double timeSinceLastReqCheck=0.;
	private Double timeSinceLastInstitutionCheck=0.;


	private Double reasonableReqCheckTime;
	private Double reasonableInstitutionCheckTime;
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
	
	/**
	 * Creates a new LCAdmin with the given parameters. 
	 * @param lc Reference to the {@link LCServer}'s client, used to issue requests.
	 * @param port Port that the server listens to.
	 * @param pubKey Public key of the standalone client.
	 */
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
		reasonableInstitutionCheckTime = Math.pow(10.,8.9 + rn.nextInt(50)/100);

		newReqs = new ArraySet<ElectricityRequirement>();
		currentReqs = new ArraySet<ElectricityRequirement>();
		deadReqs = new ArraySet<ElectricityRequirement>();
		
		updateRequirementClasses(client.handler.getReqs());
	}
	/**
	 * Determines whether an {@link ElectricityRequirement} happens in the past, present or future.
	 * @param e The requirement in question.
	 * @return An integer corresponding the the location in time of the requirement. 2 => past, 1 => present, 0 => future.
	 */
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
	/**
	 * Places the elements of the given ArraySet into three different categories depending on whether they have occurred, are occurring, or have yet to occur.
	 * @param reqs The set of {@link ElectricityRequirement}s.
	 */
	public void updateRequirementClasses(ArraySet<ElectricityRequirement> reqs)
	{
		for (ElectricityRequirement r : reqs)
		{
			switch(getRequirementCategory(r))
			{
			case -1:
				System.out.println("The requirement category algorithm is broken in LCAdmin"); //this should not be possible to reach without wibbly wobbly timey wimey stuff
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
	/**
	 * Checks the three time categories to determine if any future requirements are now live, and if any live requirements are now dead.
	 * Moves requirements into their correct categories based on their current state.
	 */
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
	/**
	 * If a ticket exists for the requirement, request the {@EDCServer} to turn on that device.
	 * @param r the requirement to be tested.
	 */
	private void checkTickets(ElectricityRequirement r) {
		ElectricityTicket q = client.handler.findMatchingTicket(r);
		if (q!=null)
		{
			q.setActive(true);
			client.setState(r.getDevice().getId(), true);
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
	/**
	 * Requests a list of all the known users from the {@link HLCServer}.
	 * @return true iff there are users in the log of users.
	 */
	private Boolean requestBulletin() {
		HashMap<String, Triple<String,InetSocketAddress, Double>> x = client.getAddresses();
		if (x!= null) {
		for (Entry<String, Triple<String,InetSocketAddress, Double>> e : x.entrySet())
		{
			
			if (e.getValue()!=null)
			{
				if (!e.getKey().equals(client.getId())) 
				{
				bulletin.add(new NamedSocket(e.getKey(),e.getValue().central,e.getValue().left));
			}
			}
		}

		bulletin.sociallyAwah = true;
		}
		return bulletin.sociallyAwah;
	}
	/**
	 * Starts the LCAdmin and hooks it on a new thread.
	 */
	public void start() {
		System.out.println("Client admin active.");
		if (t == null)
	      {
	         t = new Thread (this, (client.handler.getId()+"admin"));
	         t.start ();
	      }
	}
	/**
	 * Central polling loop that waits for set amounts of time and then performs administrative actions such as swapping tickets and updating logs of users in the system.
	 */
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
				timeSinceLastInstitutionCheck += pollingTime;
				
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
			if (timeSinceLastInstitutionCheck > reasonableInstitutionCheckTime)
			{
				
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
