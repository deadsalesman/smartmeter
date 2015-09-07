package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This decision module always accepts a trade, regardless of personal loss.
 * @author bwindo
 *
 */
public class SocialDecisionModule implements DecisionModuleIFace{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users,  String user) {
		return true;
	}

}
