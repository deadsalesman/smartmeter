package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This decision module accepts a trade if it directly benefits them in the short term.
 * @author bwindo
 *
 */
public class SelfishDecisionModule implements DecisionModuleIFace{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users,  String user) {
		return newUtility > oldUtility;
	}

}
