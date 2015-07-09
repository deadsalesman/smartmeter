package uk.ac.imperial.smartmeter.res;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityTicket implements UniqueIdentifierIFace{
	public Date start;
	public Date end;
	public double magnitude;
	public UUID ownerID;
	public UUID id;
	public ElectricityTicket(Date s, Date e, Double m, String owner)
	{
		this(s,e,m,owner, UUID.randomUUID().toString());
	}
	public ElectricityTicket(Date s, Date e, Double m, String owner, String idString)
	{
		start = s;
		end = e;
		magnitude = m;
		ownerID = UUID.fromString(owner);
		id = UUID.fromString(idString);
	}
	@Override
	public String getId() {
		return id.toString();
	}

}
