package uk.ac.imperial.smartmeter.res;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.webcomms.NamedSocket;

/**
 * This class is used to associate a probability to a list of integers.
 * 
 * @author bwindo
 *
 */
public class BulletinProbabilityGenerator {
	private Double stdev;
	private Double mean;
	private ArrayList<Integer> data;
	public BulletinProbabilityGenerator()
	{
		stdev = 0.; 
		mean = 0.;
		data = new ArrayList<Integer>();
	}
	/**
	 * Associates a list of pairs of named sockets and integers which their respective probabilities.
	 * @param input The named sockets and integers.
	 * @return A map of the sockets to their associated probabilities.
	 */
	public ArrayList<Pair<NamedSocket, Double>> generateProbabilities(ArrayList<Pair<NamedSocket,Integer>> input) throws NullPointerException
	{
		ArrayList<Pair<NamedSocket, Double>> normalised = new ArrayList<Pair<NamedSocket, Double>>();
		
		for (Pair<NamedSocket, Integer> x : input)
		{
			data.add(x.right);
		}
		stdev = calcStdDev();
		mean = calcMean();
		
		ArrayList<Double> unnormalised = new ArrayList<Double>();
		Double sum = 0.;
		for (Integer i : data)
		{
			Double p = calculateProbability(i);
			sum += p;
			unnormalised.add(p);
		}
		
		for (int i = 0; i < unnormalised.size(); i++)
		{
			normalised.add(new Pair<NamedSocket, Double>(input.get(i).left, unnormalised.get(i)/sum));
		}
		return normalised;
	}
	
	/**
	 * 
	 * @return the mean of the internal data.
	 */
	private Double calcMean() {
		Double sum = 0.;
		for (Integer i : data)
		{
			sum += i;
		}
		
		return sum/data.size();
	}
	/**
	 * 
	 * @return the standard deviation of the internal data
	 */
	private Double calcStdDev() {
		if (data.size()==1)
		{
			return 0.;
		}
		Double sumX = 0.;
		Double sumXX = 0.;
		
		for (Integer i : data)
		{
			sumX += i;
			sumXX += i*i;
		}
		Double diff = (sumXX - sumX*sumX) / (data.size()-1);
		return Math.sqrt(diff);
	}
	/**
	 * Generates the probability associated with a given integer, given the previously established distribution of data as stored internally.
	 * @param i The given integer.
	 * @return The probability associated.
	 */
	private Double calculateProbability(Integer i)
	{
		if (stdev == 0.)
		{
			return 1.;
		}
		Double normalised = (i - mean) / stdev;
		Double inverted = Phi(normalised);
		return inverted;
	}

	/**
	 * Calculates the inverse normal mapping (Z value to percentage point).
	 * 
	 * @param z the Z value
	 * @return The percentage corresponding to z.
	 */

	public static Double Phi(Double z) {
		if (z.isNaN())
		{
			return 0.0;
		}
		if (z.isInfinite())
		{
			return 0.0;
		}
		if (z < -8.0)
			return 0.0;
		if (z > 8.0)
			return 1.0;
		double sum = 0.0, term = z;
		for (int i = 3; sum + term != sum; i += 2) {
			sum = sum + term;
			term = term * z * z / i;
		}
		return 0.5 + sum * (Math.exp(-z * z / 2) / Math.sqrt(2 * Math.PI));
	}
}
