package uk.ac.imperial.smartmeter.res;

public class UniformConsumptionProfile extends ConsumptionProfile {

	@Override
	protected double shape(double time) {
		return 1;
	}
	public UniformConsumptionProfile()
	{
		super();
	}

	
}
