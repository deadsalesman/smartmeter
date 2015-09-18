package uk.ac.imperial.smartmeter.electricityprofile;

/**
 * Class to simulate the consumption profile of a constant demand system.
 * @author bwindo
 *
 */
public class UniformConsumptionProfile extends ConsumptionProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2127061124585982198L;
	/**
	 * Returns the amplitude of the consumption at a certain time.
	 * @param time The time to evaluate the shape at.
	 * @return The amplitude of the consumption at that time.
	 */
	@Override
	protected double shape(double time) {
		return 1;
	}
	public UniformConsumptionProfile()
	{
		super();
	}
	public UniformConsumptionProfile(Double amp)
	{
		super();
		amplitude = amp;
		name = "Uniform";
	}
	public UniformConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "Uniform";
	}

	
}
