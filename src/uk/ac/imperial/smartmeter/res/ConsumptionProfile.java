package uk.ac.imperial.smartmeter.res;

public abstract class ConsumptionProfile {
	private double amplitude; 
	private double duration; //in ms
	protected abstract double shape(double time); //normalised to [0,1]
	public ConsumptionProfile()
	{
		amplitude = 1;
		duration = 1000;
	}
	public double getConsumption(double time)
	{
		if ((time <= duration) && (time >= 0))
		{
			return amplitude*shape(time/duration);
		}
		else
		{
			return 0;
		}
	}
}
