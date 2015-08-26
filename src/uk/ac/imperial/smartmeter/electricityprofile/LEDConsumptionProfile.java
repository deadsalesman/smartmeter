package uk.ac.imperial.smartmeter.electricityprofile;

public class LEDConsumptionProfile extends ConsumptionProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273089289411915372L;

	@Override
	protected double shape(double time) {
		return 1;
	}
	public LEDConsumptionProfile()
	{
		super();
	}
	public LEDConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "LED";
	}

}
