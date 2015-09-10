package uk.ac.imperial.smartmeter.webcomms;

import java.util.ArrayList;

import uk.ac.imperial.smartmeter.res.BulletinProbabilityGenerator;
import uk.ac.imperial.smartmeter.res.Pair;

public class Phitest {

	/**
	 * verifies the functionality of the inverse normal distribution function.
	 * @param args
	 */
	public static void main(String[] args)
 {
		Boolean ret = true;
		ret &= diff(pPhi(0.273205323),0.607652306);
		ret &= diff(pPhi(-2.732053227),0.003147049);
		ret &= diff(pPhi(-1.912437259),0.027910066);
		ret &= diff(pPhi(-2.458847904),0.006969183);
		ret &= diff(pPhi(-0.546410645),0.292391847);
		System.out.println(ret);
		
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(-4);
		arr.add(3);
		arr.add(2);
		arr.add(-1);
		arr.add(2);

		ArrayList<Pair<NamedSocket, Integer>> input = new ArrayList<Pair<NamedSocket, Integer>>();
		for (Integer i : arr)
		{
			input.add(new Pair<NamedSocket, Integer>(new NamedSocket(null, null), i));
		}
		input = null;
		BulletinProbabilityGenerator b = new BulletinProbabilityGenerator();
		ArrayList<Pair<NamedSocket, Double>> y = b.generateProbabilities(input);
		Double sum = 0.;
		for (Pair<NamedSocket, Double> X : y)
		{
			sum += X.right;
		}
		System.out.println(sum.equals(1.));
	}
	private static Double pPhi(Double d)
	{
		return BulletinProbabilityGenerator.Phi(d);
	}
	private static Boolean diff(Double d, Double e)
	{
		return (Math.abs(d-e)<=0.00001);
	}
}
