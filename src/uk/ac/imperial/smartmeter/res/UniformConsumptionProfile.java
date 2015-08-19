package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

public class UniformConsumptionProfile extends ConsumptionProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2127061124585982198L;
	@Override
	protected double shape(double time) {
		return 1;
	}
	public UniformConsumptionProfile()
	{
		super();
	}
	public UniformConsumptionProfile(double dur, double amplitude)
	{
		super(dur,amplitude);
		name = "Uniform";
	}

	
}
