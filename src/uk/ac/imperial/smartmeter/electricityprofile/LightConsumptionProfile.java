package uk.ac.imperial.smartmeter.electricityprofile;

public class LightConsumptionProfile extends ConsumptionProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273089289411915372L;

	@Override
	protected double shape(double time) {
		return 1;
	}
	public LightConsumptionProfile()
	{
		super();
	}
	public LightConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "Light";
	}

}
