package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

public class TicketTuple implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4225756827358156471L;
	public ElectricityTicket newTkt;
	public ElectricityTicket oldTkt;
	public Boolean success = false;
	public TicketTuple(ElectricityTicket newTicket, ElectricityTicket oldTicket)
	{
		this(newTicket, oldTicket, false);
	}
	public TicketTuple(ElectricityTicket newTicket, ElectricityTicket oldTicket, Boolean result)
	{
		newTkt = newTicket;
		oldTkt = oldTicket;
		success = result;
	}
	TicketTuple()
	{
		
	}
}
