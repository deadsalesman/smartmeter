package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.res.EleGenConglomerate;

/**
 * Container for a day's worth of QuantumNodes
 * @author Ben Windo
 * @see CalendarQueue
 * @see QuantumNode
 */
public class DayNode implements TimeNode{
	ArrayList<QuantumNode> nodes;
	public static final int secInHr = 60*60;
	public static final int hrInDay = 24;
	public static final int mSecInDay = 1000*secInHr*hrInDay;
	public static final int nNodes = mSecInDay / QuantumNode.quanta;
	private Date startTime;
	private Date endTime;
	
	public int getSize()
	{
		return nodes.size();
	}
	/**
	 * Creates the necessary number of QuantumNodes, initialising their capacity with
	 * the value of the EleGenConglomerate and the time of their start
	 * @param conglom the conglomerate used to give the nodes initial values based on predicted generation
	 */
	private void initialiseNodes(EleGenConglomerate conglom)
	{
		for (int i = 0; i < nNodes; i++)
		{
			Date d = new Date(startTime.getTime()+i*QuantumNode.quanta);
			QuantumNode n = new QuantumNode(conglom.getPredictedOutput(d),d);
			nodes.add(n);
		}
	}
	/**
	 * Creates a DayNode for a 24 hour period starting at a certain time
	 * @param e the conglomerate used to specify initial values for the quantum nodes
	 * @param d the Date giving the start time
	 */
	public DayNode(EleGenConglomerate e, Date d)
	{
		nodes = new ArrayList<QuantumNode>(nNodes);
		startTime = d;
		endTime = new Date(startTime.getTime() + mSecInDay); // s to ms conversion

		initialiseNodes(e);
	}
	/**
	 * Creates a DayNode a fixed number of days offset from the specified time
	 * @param d the Date giving the start time
	 * @param i the number of days offset
	 * @param e the conglomerate used to specify initial values for the quantum nodes
	 */
	public DayNode(Date d, int i, EleGenConglomerate e)
	{
		this (e,new Date(d.getTime()+mSecInDay*i));
	}
	/**
	 * Creates a Daynode a fixed number of days offset from the current time
	 * @param i the number of days offset
	 * @param e the conglomerate used to specify initial values for the quantum nodes
	 */
	public DayNode(int i, EleGenConglomerate e)
	{
		this(e,new Date(new Date().getTime() + mSecInDay*i)); //code smell is so bad. but calendar seems worse.
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
}
