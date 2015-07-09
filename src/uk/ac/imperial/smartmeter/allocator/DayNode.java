package uk.ac.imperial.smartmeter.allocator;

import java.util.ArrayList;
import java.util.Date;

import uk.ac.imperial.smartmeter.res.EleGenConglomerate;

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
	private void initialiseNodes(EleGenConglomerate e)
	{
		for (int i = 0; i < nNodes; i++)
		{
			Date d = new Date(startTime.getTime()+i*QuantumNode.quanta);
			QuantumNode n = new QuantumNode(d);
			nodes.add(n);
		}
	}
	public DayNode(EleGenConglomerate e, Date d)
	{
		nodes = new ArrayList<QuantumNode>(nNodes);
		startTime = d;
		endTime = new Date(startTime.getTime() + mSecInDay); // s to ms conversion

		initialiseNodes(e);
	}
	public DayNode(Date d, int i, EleGenConglomerate e)
	{
		this (e,new Date(d.getTime()+mSecInDay*i));
	}
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
