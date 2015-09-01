package uk.ac.imperial.smartmeter.impl;

import java.util.UUID;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.UserAgent;
import uk.ac.imperial.smartmeter.webcomms.HLCServer;
/**
 * Acts as an intermediary for the {@link HLController} and the {@link HLCServer}
 * The controller has public methods that the server does not need to know about, but are useful for testing.
 * Thus, in the absence of proper dependency inversion, this is used to encapsulate the controller.
 * @author bwindo
 *
 */
public class HLCHandler {
	private HLController controller;
	
	public HLCHandler()
	{
		controller = new HLController();
	}
	/**
	 * Queries the database for a given user.
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @return true if the user exists in {@link HLController#agents}.
	 */
	public Boolean queryUserExistence(String userId)
	{
		return controller.queryUserExistence(userId);
	}
	/**
	 * Returns an ArraySet of all the {@link ElectricityTicket}s associated with a given user, identified by the String representation of the {@link UserAgent}'s {@link UUID}.
	 * @param userId The id of the user in question.
	 * @return An ArraySet of the tickets of interest.
	 */
	public ArraySet<ElectricityTicket> getTickets(String userId)
	{
		return controller.getTickets(userId);
	}
	/**
	 * Adds an {@link ElectricityRequirement} to the user whose id matches that of its owner.
	 * @param e The requirement in question.
	 * @return true iff there is a user whose id matches and the addition of the requirement was successful.
	 */
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
	/**
	 * Adds a {@link UserAgent} to {@link HLController#agents}
	 * @param u The {@link UserAgent} to be added.
	 * @return Success?
	 */
	public Boolean addUserAgent(UserAgent u)
	{
		return controller.addUserAgent(u);
	}
	/**
	 * Sets the electricity generation of the specified {@link UserAgent} to the specified {@link ElectricityGeneration}
	 * @param userId The representation of the {@link UUID} of the user in question.
	 * @param e The {@link ElectricityGeneration} to be adopted by the user.
	 * @return Success?
	 */
	public Boolean setUserGeneration(String userId, ElectricityGeneration e)
	{
		return controller.setUserGeneration(userId, e);
	}
	public String getUUID(String string) {
		return controller.getUUID(string);
	}
	/**
	 * Attempts to calculate tickets for the currently entered user agents
	 * @return the user agents with their tickets added
	 */
	public Boolean calculateTickets() {
		return controller.calculateTickets();
	}
	/**
	 * Removes all information from local data structures, including fields and databases.
	 * @return Success?
	 */
	public Boolean clearAll() {
		return controller.clearAll();
	}
	public String getId()
	{
		return controller.getId();
	}
	/**
	 * Attempts to extend a ticket if the ticket duration is less than the requirement duration
	 * @param oldReq
	 * @param newTkt
	 * @param oldTkt
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean extendTicket(ElectricityTicket newTkt, ElectricityRequirement oldReq, ElectricityTicket oldTkt, boolean mutable) {
		return controller.extendTicket(oldReq, newTkt,oldTkt,mutable);
	}
	/**
	 * Attempts to intensify a ticket if the ticket amplitude is less than the requirement amplitude
	 * @param newTkt
	 * @param oldReq
	 * @param oldTkt
	 * @param mutable Defines whether the underlying nodes can be changed, or whether copies should be made.
	 * @return success?
	 */
	public Boolean intensifyTicket(ElectricityTicket newTkt, ElectricityRequirement oldReq, ElectricityTicket oldTkt, boolean mutable) {
		return controller.intensifyTicket(oldReq,newTkt,oldTkt, mutable);
	}
	/**
	 * Sets the essential credentials of the server, used to sign tickets and prove identity.
	 * @param passWd The password used to access the server's private key.
	 * @param privKey The secret key of the server.
	 * @param pubKey The public key of the server.
	 */
	public void setCredentials(String passWd, String privKey, String pubKey) {
		controller.setCredentials(passWd, privKey, pubKey);
	}
}
