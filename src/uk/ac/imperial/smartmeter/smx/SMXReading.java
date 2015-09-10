package uk.ac.imperial.smartmeter.smx;


/**
 * A class representing a reading of the SMX meter. Properties are null if they were not read.
 * @author bwindo
 *
 */
public class SMXReading {
	/**
	 * The reactive power.
	 */
	public Double Q_cons;
	/**
	 * The active power.
	 */
	public Double P_cons;
	/**
	 * The voltage. (probably)
	 */
	public Double U_R;
	/**
	 * The current.
	 */
	public Double I_R;
	
	public Double getReactivePower()
	{
		return Q_cons;
	}
	public Double getActivePower()
	{
		return P_cons;
	}
	public Double getVoltage()
	{
		return U_R;
	}
	public Double getCurrent()
	{
		return I_R;
	}
	
	
}
