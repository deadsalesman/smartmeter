package uk.ac.imperial.smartmeter.electricityprofile;
/**
 * Class to simulate the consumption profile of an LED
 * @author bwindo
 *
 */
public class LedConsumptionProfile extends ConsumptionProfile {

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
	public LedConsumptionProfile()
	{
		super();
	}
	public LedConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "LED";
	}

}
