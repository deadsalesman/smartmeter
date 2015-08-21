package uk.ac.imperial.smartmeter.crypto;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class PGPKeyGen {
	PGPKeyGen()
	{
		try {
			KeyPairGen.main(new String[]{"michael","jackson"});
			KeyPairGen.main(new String[]{"ada","lovelace"});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception
	{

		PGPKeyGen x = new PGPKeyGen();
        Security.addProvider(new BouncyCastleProvider());
		//String signme = "billie jean is not my lover";
		
        PGPSigner.signFile("signme.txt", "secret.bpg", "lovelace");
        PGPSigner.signFile("signme.txt.bpg", "secret.bpg", "lovelace");
        Boolean ret = PGPSigner.verifyFile("signme.txt.bpg.bpg", "pub.bpg");
        
        for (int i = 0; i < 5; i++)
        {
        	//PGPSigner.s
        }
        
        
        System.out.println(ret?"yay":"no");
		//PGPSigner.verifyString(signme, "pub.bpg");
        //PGPSigner.main(new String[]{"-s", "signme.txt", "secret.bpg", "jackson"});
		//PGPSigner.main(new String[]{"-v", "signme.txt.bpg", "pub.bpg"});
		
	}
}
