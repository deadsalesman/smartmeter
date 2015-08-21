package uk.ac.imperial.smartmeter.crypto;


public class PGPKeyGen {
	PGPKeyGen()
	{
		try {
			KeyPairGen.main(new String[]{"-a","michael","jackson"});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args)
	{
		PGPKeyGen x = new PGPKeyGen();
	}
}
