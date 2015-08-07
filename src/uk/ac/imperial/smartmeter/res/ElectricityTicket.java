package uk.ac.imperial.smartmeter.res;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.allocator.QuantumNode;
import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityTicket implements UniqueIdentifierIFace{
	public Date start;
	public Date end;
	public double magnitude;
	public UUID ownerID;
	public UUID id;
	public UUID reqID;
	public ElectricityTicket(Date s, Date e, Double m, String owner, String reqId)
	{
		this(s,e,m,owner, reqId, UUID.randomUUID().toString());
	}
	public String toString()
	{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return df.format(this.start) + "," + df.format(this.end) + "," + this.magnitude + "," + this.ownerID.toString() + "," + this.reqID.toString() + "," + this.getId() + ",";
	}
	public double getDuration()
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
	}
	@Override
	public String getId() {
		return id.toString();
	}

}
