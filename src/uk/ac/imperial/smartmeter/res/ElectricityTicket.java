package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;
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
	public UUID ownerID;
	public UUID id;
	public UUID reqID;
	public Date getStart()
	{
		return start;
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
	public String toString()
	{
		return String.valueOf(this.start.getTime()) + "," + String.valueOf(this.end.getTime()) + "," + this.magnitude + "," + this.ownerID.toString() + "," + this.reqID.toString() + "," + this.getId() + ",";
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
	}
	public ElectricityTicket(ElectricityTicket newtkt) {
		start = DateHelper.clone(newtkt.start);
		end = DateHelper.clone(newtkt.end);
		magnitude = newtkt.magnitude;
		reqID = UUID.fromString(newtkt.reqID.toString());
		ownerID = UUID.fromString(newtkt.ownerID.toString());
		id = UUID.fromString(newtkt.id.toString());
		duration= end.getTime() - start.getTime();
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

}
