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
import uk.ac.imperial.smartmeter.electronicdevices.Uniform;
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
	private static String modFromInt(Integer value)
	{
		switch(value)
		{
		case 1:
			return "Sensible";
		case 2:
			return "Social";
		case 3:
			return "Selfish";
		case 4:
			return "Stubborn";
		case 5:
			return "Stochastic";
		default:
			return "Selfish";
				
		}
	}
	public static LCStandalone generateStandaloneSpecificDecisionProcess(Integer i, Integer value) throws RemoteException {
		Double stdev = 1.;
		Random rnd = new Random();
		Double social = rnd.nextGaussian()*stdev+1.;
		Double gen = rnd.nextGaussian()*stdev/2+0.39;
		Double prod = rnd.nextGaussian()*stdev+3.;
		LCStandalone newLC = new LCStandalone(9600+i, UUID.randomUUID().toString(),social,gen,prod);
		newLC.server.setDecisionModule(modFromInt(value));
		return newLC;
	}
	
	public static ArrayList<LCStandalone> generateRealisticSimulation() throws RemoteException 
	{
		ArrayList<LCStandalone> ret = new ArrayList<LCStandalone>();
		Integer agentType = 3;
		
		for (int i = 0; i < 8; i++)
		{
			LCStandalone a = new LCStandalone(9700+i,UUID.randomUUID().toString(),1.,5.,3.);
			allocateStandardRequirements(a);
			ret.add(a);
		}
		
		
		for (LCStandalone x : ret)
		{
			x.server.setDecisionModule(modFromInt(agentType));
		}
		
		return ret ;
	}
	public static void allocateStandardRequirements(LCStandalone user)
	{
		ElectronicConsumerDevice ed = ElectronicDeviceFactory.getDevice("LIGHT");
		Double pct = 100.;
		Random rnd = new Random();
		Double stdev = 2.;
		bindNewRequirement(user, getGaussian((double)8, stdev).intValue(), 0.05, 8000., 100.);
		bindNewRequirement(user, getGaussian((double)3, stdev).intValue(), 3., 500., 6000.);
		bindNewRequirement(user, getGaussian((double)7, stdev).intValue(), 1., 1000., 4000.);
		bindNewRequirement(user, getGaussian((double)6, stdev).intValue(), 8., 500., 1000.);
		bindNewRequirement(user, getGaussian((double)2, stdev).intValue(), 3., 1000., 1000.);
		bindNewRequirement(user, getGaussian((double)3, stdev).intValue(), 3., 1000., 2000.);
		bindNewRequirement(user, getGaussian((double)3, stdev).intValue(), 3., 1000., 3000.);
		bindNewRequirement(user, getGaussian((double)5, stdev).intValue(), 1., 400., 1000.);
		bindNewRequirement(user, getGaussian((double)9, stdev).intValue(), 1., 100., 1000.);
		bindNewRequirement(user, getGaussian((double)3, stdev).intValue(), 1., 1000., 6000.);
		bindNewRequirement(user, getGaussian((double)1, stdev).intValue(), 4., 600., 6000.);
		bindNewRequirement(user, getGaussian((double)3, stdev).intValue(), 1., 1000., 4000.);
	}
	private static void bindNewRequirement(LCStandalone user, Integer priority, Double amplitude, Double duration, Double start)
	{
		Uniform ed = new Uniform(amplitude);
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		Integer thresh = rnd.nextInt(101);
		Boolean bind = 100 >= thresh;
		
		if (bind)
		{
			TicketTestHelper.bindRequirement(user.server.client, start, duration+start, priority, ed);
		}
		
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
