package uk.ac.imperial.smartmeter.res;

public class UniformConsumptionProfile extends ConsumptionProfile {

	@Override
	protected double shape(double time) {
		return 1;
	}
	public UniformConsumptionProfile(double dur, double amplitude)
	{
		super(dur);
		name = "Uniform";
	}

	
}
