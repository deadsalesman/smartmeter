package uk.ac.imperial.smartmeter.electricityprofile;

public class StoveConsumptionProfile extends ConsumptionProfile {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6273089289411915372L;

	@Override
	protected double shape(double time) {
		return 1;
	}
	public StoveConsumptionProfile()
	{
		super();
	}
	public StoveConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "Stove";
	}

}
