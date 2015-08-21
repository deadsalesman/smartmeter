package uk.ac.imperial.smartmeter.crypto;

import org.bouncycastle.openpgp.examples.SignedFileProcessor;


public class PGPKeyGen {
	PGPKeyGen()
	{
		try {
			KeyPairGen.main(new String[]{"michael","jackson"});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception
	{
		PGPKeyGen x = new PGPKeyGen();
		
		String signme = "billie jean is not my lover";
		signme = PGPSigner.signString(signme, "secret.bpg",  "jackson", true);
		PGPSigner.verifyString(signme, "pub.bpg");
		SignedFileProcessor.main(new String[]{"-s", "signme.txt", "secret.bpg", "jackson"});
		
		SignedFileProcessor.main(new String[]{"-v", "signme.txt.bpg", "pub.bpg"});
		
	}
}
