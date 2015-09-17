package uk.ac.imperial.smartmeter.electricityprofile;

/**
 * Class to simulate the consumption profile of an incandescent light.
 * @author bwindo
 *
 */
public class LightConsumptionProfile extends ConsumptionProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273089289411915372L;
	/**
	 * Returns the amplitude of the consumption at a certain time.
	 * @param time The time to evaluate the shape at.
	 * @return The amplitude of the consumption at that time.
	 */
	@Override
	protected double shape(double time) {
		return 1;
	}
	public LightConsumptionProfile()
	{
		super();
	}
	public LightConsumptionProfile(Double amp)
	{
		super();
		amplitude = amp;
		name = "Light";
	}
	public LightConsumptionProfile(double dur, double amp)
	{
		super(dur,amp);
		amplitude =amp;
		name = "Light";
	}

}
