package uk.ac.imperial.smartmeter.crypto;

import org.bouncycastle.openpgp.examples.RSAKeyPairGenerator;

public class PGPKeyGen {
	PGPKeyGen()
	{
		try {
			RSAKeyPairGenerator.main(new String[]{"-a","michael","jackson"});
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
