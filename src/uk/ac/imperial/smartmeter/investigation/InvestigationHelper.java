package uk.ac.imperial.smartmeter.investigation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import uk.ac.imperial.smartmeter.autonomous.LCStandalone;
import uk.ac.imperial.smartmeter.decisions.DecisionModuleFactory;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicConsumerDevice;
import uk.ac.imperial.smartmeter.electronicdevices.ElectronicDeviceFactory;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.tests.allocator.TicketTestHelper;

/**
 * This helper class contains assorted functions that make the overall investigation process simpler.
 * @author bwindo
 *
 */
public class InvestigationHelper {
	public static ElectricityRequirement generateRandomRequirement() {
		return null;
	}
	public static ElectricityRequirement bindRandomRequirement() {
		return null;
	}
	public static LCStandalone generateStandaloneRandomDecisionProcess(Integer i) throws RemoteException {
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		return generateStandaloneSpecificDecisionProcess(i, rnd.nextInt(DecisionModuleFactory.getNModules()+1));
	}
	public static LCStandalone generateStandaloneSpecificDecisionProcess(Integer i, Integer value) throws RemoteException {
		LCStandalone newLC = new LCStandalone(9600+i, UUID.randomUUID().toString(),1.,0.39,3.);
		switch(value)
		{
		case 1:
			newLC.server.setDecisionModule("Sensible");
			break;
		case 2:
			newLC.server.setDecisionModule("Social");
			break;
		case 3:
			newLC.server.setDecisionModule("Selfish");
			break;
		case 4:
			newLC.server.setDecisionModule("Stubborn");
			break;
		case 5:
			newLC.server.setDecisionModule("Stochastic");
			break;
		default:
			newLC.server.setDecisionModule("Selfish");
			break;
				
		}
		return newLC;
	}
	public static void allocateManyLights(LCStandalone lcStandalone, Integer nLights, Integer pct) {
		Integer total = 0;
		for (int i = 0; i < nLights; i++)
		{
			ElectronicConsumerDevice ed = ElectronicDeviceFactory.getDevice("LIGHT");
			total += stochasticBindRequirementRandomOffset(lcStandalone, i*20., 1000., 3, ed, pct, 2.) ? 1:0 ;
		}
		
		//System.out.println(total);
		
		
	}
	private static Boolean stochasticBindRequirementRandomOffset(LCStandalone lc, Double start, Double duration, Integer priority, ElectronicConsumerDevice ed, Integer percentage, Double stdev)
	{
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		Integer thresh = rnd.nextInt(101);
		Boolean bind = percentage >= thresh;
		Double begin = getGaussian(start, stdev);
		
		if (bind)
		{
			TicketTestHelper.bindRequirement(lc.server.client, begin, duration+begin, priority, ed);
		}
		
		return bind;
		
	}
	private static Double getGaussian(Double mean, Double stdev)
	{
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		
		return  rnd.nextGaussian()*stdev + mean;
	}
	
	public static Boolean allocateTickets(ArrayList<LCStandalone> clients)
	{
		Boolean calced = false;
		int i = 0;
		while((!calced)&&(i < clients.size()))
		{
			try{
			System.out.println(clients.get(i).hashCode());
			clients.get(i).server.client.GodModeCalcTKTS();
			calced = true;
			}catch (Exception e){}
			i++;
		}
		return calced;
	}
	public static void allocateManyLightsRandomPriorities(LCStandalone temp, int nLights, int pct) {
		Integer total = 0;
		Double duration = 100.;
		for (int i = 0; i < nLights; i++)
		{
			ElectronicConsumerDevice ed = ElectronicDeviceFactory.getDevice("LIGHT");
			total += stochasticBindRequirementRandomPriorities(temp, i*(duration+1), i*(duration+1)+duration, 3, ed, pct, 3.) ? 1:0 ;
		}
	}
	private static boolean stochasticBindRequirementRandomPriorities(LCStandalone lc, Double start, Double duration, Integer priority, ElectronicConsumerDevice ed, Integer percentage, Double stdev) {
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		Integer thresh = rnd.nextInt(101);
		Boolean bind = percentage >= thresh;
		Double begin = getGaussian((double)priority, stdev);
		
		if (bind)
		{
			TicketTestHelper.bindRequirement(lc.server.client, start, duration,(int)(priority+begin), ed);
		}
		
		return bind;
		
	}
}
