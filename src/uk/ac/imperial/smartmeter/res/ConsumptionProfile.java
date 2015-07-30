package uk.ac.imperial.smartmeter.res;

import java.util.Date;

public abstract class ConsumptionProfile {
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
