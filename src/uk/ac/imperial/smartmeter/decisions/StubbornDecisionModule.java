package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This decision module never accepts a trade, instead preferring to keep the existing ticket.
 * @author bwindo
 *
 */
public class StubbornDecisionModule implements DecisionModuleIFace{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users, String user) {
		return false;
	}

}
