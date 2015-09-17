package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.LCServer;

/**
 * This class creates and returns a DecisionModuleIFace to be used in determining whether offers of tickets are accepted.
 * @author bwindo
 * @see LCServer
 */
public class DecisionModuleFactory {
	public static DecisionModuleIFace getDecisionModule(String moduleType) throws IllegalArgumentException
	{
		switch(moduleType)
		{
		case "Sensible":
			return new SensibleDecisionModule();
		case "Social":
			return new SocialDecisionModule();
		case "Selfish":
			return new SelfishDecisionModule();
		case "Stubborn":
			return new StubbornDecisionModule();
		case "Stochastic":
			return new StochasticDecisionModule();
		default:
			throw new IllegalArgumentException();
		}
	}
	public static Integer getNModules()
	{
		return 5;
	}
}
