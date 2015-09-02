package uk.ac.imperial.smartmeter.res;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

/**
 * A class representing a provisioned quantity of electricity for a given time.
 * @author bwindo
 *
 */
public class ElectricityTicket implements UniqueIdentifierIFace, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7182591298140167129L;
	private Date start;
	private Date end;
	public double magnitude;
	private double duration;
	/**
	 * A boolean representing whether the ticket is currently being used to provision a device.
	 */
	private Boolean active = false;
	/**
	 * A data structure containing the complete history of the ticket for each time that the ticket has been signed by a node.
	 * String = userId of the Signatory.
	 * Date = time of signing.
	 * ElectricityTicket = ticket at time of signing.
	 * byte[] = signature.
	 */
	private ArrayList<Quadruple<String,Date,ElectricityTicket,byte[]>> signatures;
	/**
	 * The id of the ticket's owner.
	 */
	public UUID ownerID;
	/**
	 * The id of the ticket.
	 */
	public UUID id;
	/**
	 * The id of the {@link ElectricityRequirement} associated with the ticket.
	 */
	public UUID reqID;
	public Date getStart()
	{
		return start;
	}
	/**
	 * A method used exclusively for testing. It invalidates all signatures by modifying the recorded ElectricityTickets. 
	 */
	public void ruinSignatureValidity() //used in testing. should not ever be used outside testing. you will make babies cry. why would you even do something like that? you evil person.
	{
		for (Quadruple<String, Date, ElectricityTicket, byte[]> b : signatures)
		{
			b.rightmid.magnitude += 2.;
		}
	}
	public int getNSignatures()
	{
		return signatures.size();
	}
	public Date getEnd()
	{
		return end;
	}
	public void setStart(Date d)
	{
		start = d;
		setDuration();
	}
	public void setEnd(Date d)
	{
		end = d;
		setDuration();
	}
	public double getDuration()
	{
		return duration;
	}
	/**
	 * Recomputes the duration based on the current start and end times.
	 */
	public void setDuration()
	{
		duration= end.getTime() - start.getTime();
	}
	/**
	 * Generates a new ElectricityTicket based on the given parameters.
	 * @param start The start time of the ticket.
	 * @param end The end time of the ticket.
	 * @param magnitude The magnitude of the ticket.
	 * @param owner The owner of the ticket.
	 * @param reqId The identity of the {@link ElectricityRequirement} to be associated with the ticket.
	 */
	public ElectricityTicket(Date start, Date end, Double magnitude, String owner, String reqId)
	{
		this(start,end,magnitude,owner, reqId, UUID.randomUUID().toString());
	}
	/**
	 * Adds a new signature to the repository of signatures.
	 * @param signer The entity that is signing the ticket. 
	 * @param sig The actual signature.
	 */
	public void addSignature(String signer, String sig)
	{
		//Store the current state of the ticket
		ElectricityTicket savepoint = new ElectricityTicket(this);
		signatures.add(new Quadruple<String, Date, ElectricityTicket, byte[]>(signer, new Date(), savepoint,sig.getBytes()));
	}
	/**
	 * @return A String representation of the current ElectricityTicket.
	 */
	public String toString()
	{
		String ret =  String.valueOf(this.start.getTime()) + "," + String.valueOf(this.end.getTime()) + "," + this.magnitude + "," + this.ownerID.toString() + "," + this.reqID.toString() + "," + this.getId() + ",";
		
		for (Quadruple<String, Date, ElectricityTicket, byte[]> b : signatures)
		{
			ret += b.left;
			ret += b.leftmid.getTime();
			ret += b.rightmid.toString();
			ret += new String(b.right);
			ret += ",";
		}
		return ret;
	}
	/**
	 * Generates a String representation of a past ElectricityRequirement.
	 * @param depth An integer determining how many layers back in time should be printed.
	 * @return A string containing the history of the ElectricityTicket at all layers below the targeted one.
	 */
	public String toString(int depth)
	{
		String ret =  String.valueOf(
				signatures.get(depth).rightmid.start.getTime()) + ","+ 
				String.valueOf(signatures.get(depth).rightmid.end.getTime()) + "," + 
				signatures.get(depth).rightmid.magnitude + "," + 
				signatures.get(depth).rightmid.ownerID.toString() + "," + 
				signatures.get(depth).rightmid.reqID.toString() + "," + 
				signatures.get(depth).rightmid.getId() + ",";
		
		for (int i = 0; i < depth; i++)
		{
			ret += signatures.get(i).left;
			ret += signatures.get(i).leftmid.getTime();
			ret += signatures.get(i).rightmid.toString();
			ret += new String(signatures.get(i).right);
			ret += ",";
		}
		return ret;
	}
	/**
	 * @return The duration of the tickets in units of {@link QuantumNode#quanta} ms.
	 */
	public double getQuantisedDuration()
	{
		return (end.getTime() - start.getTime())/QuantumNode.quanta;
	}
	/**
	 * 
	 * Generates a new ElectricityTicket based on the given parameters.
	 * @param s The start time of the ticket.
	 * @param e The end time of the ticket.
	 * @param m The magnitude of the ticket.
	 * @param owner The owner of the ticket.
	 * @param reqId The identity of the {@link ElectricityRequirement} to be associated with the ticket.
	 * @param idString The {@link UUID} to represent the new ticket.
	 */
	
	public ElectricityTicket(Date s, Date e, Double m, String owner, String reqId, String idString)
	{
		start = s;
		end = e;
		magnitude = m;
		reqID = UUID.fromString(reqId);
		ownerID = UUID.fromString(owner);
		id = UUID.fromString(idString);
		duration= e.getTime() - s.getTime();
		signatures = new ArrayList<Quadruple<String, Date, ElectricityTicket, byte[]>>();
	}
	/**
	 * Clones an existing ticket.
	 * @param newtkt The ticket to be cloned.
	 */
	public ElectricityTicket(ElectricityTicket newtkt) {
		start = DateHelper.clone(newtkt.start);
		end = DateHelper.clone(newtkt.end);
		magnitude = newtkt.magnitude;
		reqID = UUID.fromString(newtkt.reqID.toString());
		ownerID = UUID.fromString(newtkt.ownerID.toString());
		id = UUID.fromString(newtkt.id.toString());
		duration= end.getTime() - start.getTime();
		signatures = new ArrayList<Quadruple<String, Date, ElectricityTicket, byte[]>>();
	}
	/**
	 * Prints all signatures out to the console.
	 */
	public void spamSigs()
	{
		int i = 0;
		for (Quadruple<String, Date, ElectricityTicket, byte[]> b : signatures)
		{
			i++;
			System.out.println("Printing signature" + i);

			System.out.println(b.left);
			System.out.println(b.leftmid.getTime());
			System.out.println(b.rightmid);
			System.out.println(new String(b.right));
		}
	}
	@Override
	public String getId() {
		return id.toString();
	}
	/**
	 * Modifies the ticket to resemble an existing ticket.
	 * @param tempNew The ticket to copy from.
	 */
	public void clone(ElectricityTicket tempNew) {
		modifyID(tempNew);
		modifyTimings(tempNew);
	}
	/**
	 * Changes ownership and associated requirement of the ticket to that of a different ticket.
	 * @param tempNew The ticket to copy from.
	 */
	public void modifyID(ElectricityTicket tempNew)
	{
		reqID = UUID.fromString(tempNew.reqID.toString());
		ownerID = UUID.fromString(tempNew.ownerID.toString());

	}
	/**
	 * Changes timing, magnitude, and id of the ticket to that of a different ticket.
	 * @param tempNew The ticket to copy from.
	 */
	public void modifyTimings(ElectricityTicket tempNew) {
		start = DateHelper.clone(tempNew.start);
		end = DateHelper.clone(tempNew.end);
		magnitude = tempNew.magnitude;

		id = UUID.fromString(tempNew.id.toString());
		setDuration();
	}
	/**
	 * Returns the signature at a certain depth.
	 * @param depth The depth to consider.
	 * @return The signature at that depth.
	 */
	public Quadruple<String, Date, ElectricityTicket, byte[]> getSignature(int depth)
	{
		return signatures.get(depth);
	}
	/**
	 * 
	 * @return All signatures stored in the ticket.
	 */
	public ArrayList<Quadruple<String, Date, ElectricityTicket, byte[]>> getSignatures()
	{
		return signatures;
	}
	/**
	 * For a given depth, generates a hash of all the data below that depth and writes it to file. Also writes the signature at that depth to a file.
	 * This is used in {@link SignatureHelper#verifyTicket(ElectricityTicket t)} as part of the verification process, if the hash matches that of the decrypted signature then the ticket has not been altered.
	 * @param depth
	 */
	public void writeLog(int depth) {
		try {
			FileOutputStream oFile = new FileOutputStream(this.getId() + ".v");
			FileOutputStream oSig = new FileOutputStream(this.getId() + ".s");
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update("geronimo".getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] digest = md.digest();
			
			md = MessageDigest.getInstance("SHA-256");
			md.update(this.toString(depth).getBytes("UTF-8"));
			md.update((byte)'_');
			md.update(digest);
			
			oFile.write(md.digest());
			oSig.write(getSignature(depth).right);
			
			oSig.close();
			oFile.close();
			
		} catch (IOException | NoSuchAlgorithmException e) {
		}
		
		
		
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

}
