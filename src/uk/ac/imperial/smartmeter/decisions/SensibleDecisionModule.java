package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This decision module will accept trades that hurt it in the short term up to a certain point, where it will refuse further ones.
 * @author bwindo
 *
 */
public class SensibleDecisionModule implements DecisionModuleIFace{

	/**
	 * This is the amount of tolerance the agent has for each individual user. Higher values represent greater tolerance.
	 */
	public Double credit;
	
	SensibleDecisionModule()
	{
		credit = 0.5;
	}
	
	SensibleDecisionModule(Double value)
	{
		credit = value;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users,  String user) {
		Double history = users.getHistory(user);
		Double utilityChange = newUtility - oldUtility;
		if (history + utilityChange <= credit) {
			users.setHistory(user,history + utilityChange);
			return true;
		}
		return false;
	}

}
