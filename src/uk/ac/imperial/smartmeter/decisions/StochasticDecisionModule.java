package uk.ac.imperial.smartmeter.decisions;

import java.util.Date;
import java.util.Random;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This decision module accepts or denies a transaction on a random basis.
 * @author bwindo
 *
 */
public class StochasticDecisionModule implements DecisionModuleIFace{

	StochasticDecisionModule()
	{
		threshold = 50;
	}
	StochasticDecisionModule(Integer value)
	{
		threshold = value;
	}
	/**
	 * {@inheritDoc}
	 */
	public Integer threshold;
	@Override
	public Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users,  String user) {
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		Integer randomInt = rnd.nextInt(101);
		
		return (randomInt <= 50);
	}

}
