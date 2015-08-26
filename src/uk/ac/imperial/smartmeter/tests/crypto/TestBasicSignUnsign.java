package uk.ac.imperial.smartmeter.tests.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.PGPKeyGen;
import uk.ac.imperial.smartmeter.crypto.PGPSigner;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestBasicSignUnsign extends GenericTest {

	@Override
	public boolean doTest() throws Exception {
		String id = "basicsignunsign";
		String pass = "mypassword";
		String testFile = "basicsignunsign";
		String testFileData = "helllo ia m ads asds as ddsaatata";
		
		FileOutputStream fOut = new FileOutputStream(testFile+".xtx");
		
		for (byte b : testFileData.getBytes()){fOut.write(b);}
		
		Twople<String, String> y = KeyPairGen.genKeySet(id, pass);
		PGPKeyGen.printPubKey(id,y.right);
		PGPKeyGen.printSecKey(id,y.left);
		PGPSigner.signFile(testFile+".xtx", id +"_secret.bpg", pass);

		Boolean ret = PGPSigner.verifyFile(testFile + ".xtx.bpg", id + "_pub.bpg");
		
		FileInputStream fIn = new FileInputStream(testFile+".xtx");
		
		byte temp = 0;
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		while((temp=(byte) fIn.read())!=-1){bytes.add(temp);}
		
		String readData = PGPKeyGen.stringFromArrayList(bytes);
		fOut.close();
		fIn.close();
		return ret && readData.equals(testFileData);
		
	}

}
