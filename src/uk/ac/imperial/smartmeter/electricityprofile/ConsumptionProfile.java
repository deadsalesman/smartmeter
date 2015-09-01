package uk.ac.imperial.smartmeter.electricityprofile;

import java.io.Serializable;
import java.util.Date;
/**
 * Class to simulate the consumption profile of an electronic device.
 * @author bwindo
 *
 */
public abstract class ConsumptionProfile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8339153712870292778L;
	protected double amplitude; 
	protected double duration; //in ms
	protected String name;
	/**
	 * Returns the amplitude of the consumption at a certain time.
	 * @param time The time to evaluate the shape at.
	 * @return The amplitude of the consumption at that time.
	 */
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
	/**
	 * Gets the consumption at a certain time.
	 *
	 * @param start The start time of the consumption.
	 * @param offset The time the consumption is to be observed at.
	 * @return The value of the consumption at the time observed.
	 */
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
