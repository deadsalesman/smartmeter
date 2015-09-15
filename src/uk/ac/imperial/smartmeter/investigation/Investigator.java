package uk.ac.imperial.smartmeter.investigation;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.webcomms.EDCServer;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;

public class Investigator {
	public Integer nAgents = 50;
	public Boolean selfHost = true;
	public void investigate() throws Exception
	{
	if (selfHost)
	{
		HLCServer h  = new HLCServer(6666);
		Thread hThread = new Thread(h);
		EDCServer e = new EDCServer(7777);
		Thread eThread = new Thread(e);
		
		hThread.start();
		eThread.start();
	}
		ArrayList<LCStandalone> standalones = new ArrayList<LCStandalone>();
		
		for (int i = 0; i < nAgents; i++)
		{
			standalones.add(InvestigationHelper.generateStandaloneRandomDecisionProcess());
			InvestigationHelper.allocateRequirements(standalones.get(i));
		}
		
		
	}
}
