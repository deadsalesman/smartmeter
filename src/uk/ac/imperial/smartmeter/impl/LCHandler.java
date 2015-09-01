package uk.ac.imperial.smartmeter.impl;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.LCClient;
/**
 * Acts as an intermediary for the {@link LController} and the {@link LCClient}
 * The controller has public methods that the server does not need to know about, but are useful for testing.
 * Thus, in the absence of proper dependency inversion, this is used to encapsulate the controller.
 * @author bwindo
 * @see LController
 * @see LCClient
 */
public class LCHandler {
private LController controller;
	private String id;
	/**
	 * Initialises the controller with the specified parameters used for the Rescher distributive justice allocator.
	 * 
	 * @param username The username to be used by the controller to represent the UserAgent.
	 * @param password The password associated with signing tickets and authenticating identity.
	 * @param social The weighting allocated to social worth for the user.
	 * @param generation The weighting allocated to electricity generation for the user.
	 * @param economic The weighting allocated to economic worth for the user.
	 */
	public LCHandler(String username,String password,Double social, Double generation, Double economic)
	{
		controller = new LController(username, password, social, generation, economic);
		id = controller.getId();
	}
	/**
	 * Adds an {@link ElectricityRequirement} to the controller.
	 * @param e The requirement in question.
	 * @return Success?
	 */
	public Boolean setRequirement(ElectricityRequirement e)
	{
		return controller.addRequirement(e);
	}
	public String getSalt(){
		return controller.getSalt();
	}
	public String getHash(){
		return controller.getHash();
	}
	/**
	 * Finds the electricity tickets that are active at the same time as a given {@link ElectricityRequirement}.
	 * @param req The requirement in question.
	 * @return An {@link ArraySet} of the tickets that conflict with the given requirement.
	 */
	public ArraySet<ElectricityTicket> findCompetingTickets(ElectricityRequirement req)
	{
		return controller.findCompetingTickets(req);
	}
	/**
	 * Gets all the  {@link ElectricityRequirement} objects associated with the controller.
	 * @return An {@link ArraySet} of  {@link ElectricityRequirement}.
	 */
	public ArraySet<ElectricityRequirement> getReqs()
	{
		return controller.getReqs();
	}
	/**
	 * Gets the  {@link ElectricityTicket} object associated with the controller that is allocated to a given {@link ElectricityRequirement}.
	 * @param req The given {@link ElectricityRequirement}.
	 * @return The {@link ElectricityTicket} that matches the given requirement.
	 */
	public ElectricityTicket findMatchingTicket(ElectricityRequirement req)
	{
		return controller.findMatchingTicket(req);
	}
	/**
	 * Gets the  {@link ElectricityRequirement} object associated with the controller that has a given {@link ElectricityTicket} allocated to it.
	 * @param tkt The given {@link ElectricityTicket}.
	 * @return The {@link ElectricityRequirement} that matches the given requirement.
	 */
	public ElectricityRequirement findMatchingRequirement(ElectricityTicket tkt)
	{
		return controller.findMatchingRequirement(tkt);
	}
	public String getId() {
		return id;
	}
	public boolean setGeneration(ElectricityGeneration e) {
		return controller.setEleGen(e);
	}
	/**
	 * Forces the controller to replace a ticket in an existing {@link ElectricityTicket} : {@link ElectricityRequirement} pair with a new one.
	 * @param t The ticket to replace the old one with.
	 * @return Success?
	 */
	public boolean forceNewTicket(ElectricityTicket t) {
		return controller.forceNewTicket(t);
	}
	/**
	 * Adds a {@link ElectricityTicket} to the controller iff there is an {@link ElectricityRequirement} that it is satisfying.
	 * @param t The ticket to be added.
	 * @return Success?
	 */
	public boolean setTicket(ElectricityTicket t) {
		return controller.setTicket(t);
	}
	/**
	 * 
	 * @return all {@link ElectricityTicket} objects in the controller, which are being used to satisfy a {@link ElectricityRequirement}.
	 */
	public ArraySet<ElectricityTicket> getTkts()
	{
		return controller.getTkts();
	}
	/**
	 * Queries the controller to see if it has any {@link ElectricityRequirement}s which do not have an associated {@link ElectricityTicket} capable of satisfying their requirements.
	 * @return true iff there are no unsatisfied requirements
	 */
	public Boolean queryUnsatisfiedReqs() {
		return controller.queryUnsatisfiedReqs();
	}
	/**
	 * 
	 * @return an ArrayList containing all {@link ElectricityTicket} objects that have been marked as not having sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 */
	public ArrayList<ElectricityTicket> getUnhappyTickets() {
		
		return controller.getUnhappyTickets();
	}
	/**
	 * Queries the controller whether there are any {@link ElectricityTicket} objects that do not have sufficient utility for the {@link ElectricityRequirement} associated to be fully satisfied.
	 * @return true if there any requirements that are not being sufficiently satisfied.
	 */
	public boolean queryUnhappyTickets() {
		return controller.queryUnhappyTickets();
	}
}
