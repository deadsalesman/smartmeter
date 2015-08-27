package uk.ac.imperial.smartmeter.electricityprofile;

import java.io.Serializable;
import java.util.Date;

public abstract class ConsumptionProfile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8339153712870292778L;
	protected double amplitude; 
	protected double duration; //in ms
	protected String name;
	protected abstract double shape(double time); //normalised to [0,1]
	public ConsumptionProfile(double dur)
	{
		amplitude = 1;
		duration = dur;
	}
	public ConsumptionProfile(double dur,double amp)
	{
		amplitude = amp;
		duration = dur;
	}
	public ConsumptionProfile() {
	}

	public void setMaxConsumption(double amp)
	{
		amplitude = amp;
	}
	public double getMaxConsumption()
	{
		return amplitude;
	}
	public double getConsumption(Date start, Date offset)
	{
		Long time = offset.getTime() - start.getTime();
		if ((time <= duration) && (time >= 0))
		{
			return amplitude*shape(time/duration);
		}
		else
		{
			return 0;
		}
	}
	public String getName() {
		return name;
	}
}
