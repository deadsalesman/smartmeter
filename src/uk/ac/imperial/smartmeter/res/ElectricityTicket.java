package uk.ac.imperial.smartmeter.res;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.interfaces.UniqueIdentifierIFace;

public class ElectricityTicket implements UniqueIdentifierIFace{
	public Date start;
	public Date end;
	public double magnitude;
	public UUID id;
	ElectricityTicket(Date s, Date e, Double m)
	{
		id = UUID.randomUUID();
	}
	@Override
	public String getId() {
		return id.toString();
	}

}
