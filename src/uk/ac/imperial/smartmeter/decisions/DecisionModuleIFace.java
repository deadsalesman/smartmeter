package uk.ac.imperial.smartmeter.decisions;

import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * This interface specifies how a particular agent will respond to a request to trade tickets.
 * @author bwindo
 *
 */
public interface DecisionModuleIFace {
	/**
	 * Determines if the change in utility is acceptable given the previous history with the user who offered the ticket in the first place and the specific needs of the decision module itself.
	 * @param newUtility The utility given to the new pairing of ticket and requirement.
	 * @param oldUtility The utility given to the old pairing of ticket and requirement.
	 * @param users A list of all known clients, and their associated social capital.
	 * @param user The specific user who has offered the new ticket.
	 * @return true if the deal is sufficiently favourable.
	 */
	Boolean decideUtility(Double newUtility, Double oldUtility, UserAddressBook users, String user);
}


