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
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityTicket implements UniqueIdentifierIFace, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7182591298140167129L;
	private Date start;
	private Date end;
	public double magnitude;
	private double duration;
	private ArrayList<Triple<Date,ElectricityTicket,byte[]>> signatures;
	public UUID ownerID;
	public UUID id;
	public UUID reqID;
	public Date getStart()
	{
		return start;
	}
	public void ruinSignatureValidity() //used in testing. should not ever be used outside testing
	{
		for (Triple<Date,ElectricityTicket,byte[]> b : signatures)
		{
			b.central.magnitude += 2.;
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
	public void setDuration()
	{
		duration= end.getTime() - start.getTime();
	}
	public ElectricityTicket(Date s, Date e, Double m, String owner, String reqId)
	{
		this(s,e,m,owner, reqId, UUID.randomUUID().toString());
	}
	public void addSignature(String sig)
	{
		ElectricityTicket savepoint = new ElectricityTicket(this);
		signatures.add(new Triple<Date, ElectricityTicket, byte[]>(new Date(), savepoint,sig.getBytes()));
	}
	public String toString()
	{
		String ret =  String.valueOf(this.start.getTime()) + "," + String.valueOf(this.end.getTime()) + "," + this.magnitude + "," + this.ownerID.toString() + "," + this.reqID.toString() + "," + this.getId() + ",";
		
		for (Triple<Date, ElectricityTicket, byte[]> b : signatures)
		{
			ret += b.left.getTime();
			ret += b.central.toString();
			ret += new String(b.right);
			ret += ",";
		}
		return ret;
	}
	
	public String toString(int depth)
	{
		String ret =  String.valueOf(
				signatures.get(depth).central.start.getTime()) + ","+ 
				String.valueOf(signatures.get(depth).central.end.getTime()) + "," + 
				signatures.get(depth).central.magnitude + "," + 
				signatures.get(depth).central.ownerID.toString() + "," + 
				signatures.get(depth).central.reqID.toString() + "," + 
				signatures.get(depth).central.getId() + ",";
		
		for (int i = 0; i < depth; i++)
		{
			ret += signatures.get(i).left.getTime();
			ret += signatures.get(i).central.toString();
			ret += new String(signatures.get(i).right);
			ret += ",";
		}
		return ret;
	}
	public double getQuantisedDuration()
	{
		return (end.getTime() - start.getTime())/QuantumNode.quanta;
	}
	public ElectricityTicket(Date s, Date e, Double m, String owner, String reqId, String idString)
	{
		start = s;
		end = e;
		magnitude = m;
		reqID = UUID.fromString(reqId);
		ownerID = UUID.fromString(owner);
		id = UUID.fromString(idString);
		duration= e.getTime() - s.getTime();
		signatures = new ArrayList<Triple<Date,ElectricityTicket,byte[]>>();
	}
	public ElectricityTicket(ElectricityTicket newtkt) {
		start = DateHelper.clone(newtkt.start);
		end = DateHelper.clone(newtkt.end);
		magnitude = newtkt.magnitude;
		reqID = UUID.fromString(newtkt.reqID.toString());
		ownerID = UUID.fromString(newtkt.ownerID.toString());
		id = UUID.fromString(newtkt.id.toString());
		duration= end.getTime() - start.getTime();
		signatures = new ArrayList<Triple<Date,ElectricityTicket,byte[]>>();
	}
	public void spamSigs()
	{
		int i = 0;
		for (Triple<Date, ElectricityTicket, byte[]> b : signatures)
		{
			i++;
			System.out.println("Printing signature" + i);

			System.out.println(b.left.getTime());
			System.out.println(b.central);
			System.out.println(new String(b.right));
		}
	}
	@Override
	public String getId() {
		return id.toString();
	}
	public void clone(ElectricityTicket tempNew) {
		modifyID(tempNew);
		modifyTimings(tempNew);
	}
	public void modifyID(ElectricityTicket tempNew)
	{
		reqID = UUID.fromString(tempNew.reqID.toString());
		ownerID = UUID.fromString(tempNew.ownerID.toString());

	}
	public void modifyTimings(ElectricityTicket tempNew) {
		start = DateHelper.clone(tempNew.start);
		end = DateHelper.clone(tempNew.end);
		magnitude = tempNew.magnitude;

		id = UUID.fromString(tempNew.id.toString());
		setDuration();
	}
	public byte[] getSignature(int depth)
	{
		return signatures.get(depth).right;
	}
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
			oSig.write(getSignature(depth));
			
			oSig.close();
			oFile.close();
			
		} catch (IOException | NoSuchAlgorithmException e) {
		}
		
		
		
	}

}
