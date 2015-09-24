package uk.ac.imperial.smartmeter.investigation;

import java.util.Date;
import java.util.Random;

public class Gausstest {
	public static void main(String[] args)
	{
		Random rnd = new Random();
		rnd.setSeed((new Date()).getTime());
		Double total = 0.;
		for (int i = 0; i < 10000; i++)
		{
			total += rnd.nextGaussian();
		}
		System.out.println(total);
	}

}
