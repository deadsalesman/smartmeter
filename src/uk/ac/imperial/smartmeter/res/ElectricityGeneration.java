package uk.ac.imperial.smartmeter.res;

import java.util.Date;

public class ElectricityGeneration {
	private double currentOutput;
	//private kalmanFilter predictor;
	public ElectricityGeneration(Double amplitude)
	{
		currentOutput = amplitude;
	}
	public double getMaxOutput()
	{
		return currentOutput;
		
	}
	public double getCurrentOutput()
	{
		return currentOutput;
	}
	public double getPredictedOutput(Date elapsed)
	{
		return currentOutput; //assume input is constant
	}
}
