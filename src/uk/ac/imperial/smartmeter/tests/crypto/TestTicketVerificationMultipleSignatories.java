package uk.ac.imperial.smartmeter.tests.crypto;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.PGPKeyGen;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestTicketVerificationMultipleSignatories extends GenericTest {

	@Override
	public boolean doTest() throws Exception {
		String id1 = "ticketverification1";
		String pass1 = "tktpass1";
		String id2= "ticketverification2";
		String pass2 = "tktpass2";
		String id3 = "ticketverification3";
		String pass3 = "tktpass3";
		String id4= "ticketverification4";
		String pass4 = "tktpass4";
		
		Twople<String, String> y1 = KeyPairGen.genKeySet(id1, pass1);
		PGPKeyGen.printPubKey(id1,y1.right);
		PGPKeyGen.printSecKey(id1,y1.left);
		Twople<String, String> y2 = KeyPairGen.genKeySet(id2, pass2);
		PGPKeyGen.printPubKey(id2,y2.right);
		PGPKeyGen.printSecKey(id2,y2.left);
		Twople<String, String> y3 = KeyPairGen.genKeySet(id3, pass3);
		PGPKeyGen.printPubKey(id3,y3.right);
		PGPKeyGen.printSecKey(id3,y3.left);
		Twople<String, String> y4 = KeyPairGen.genKeySet(id4, pass4);
		PGPKeyGen.printPubKey(id4,y4.right);
		PGPKeyGen.printSecKey(id4,y4.left);
		
		
		ElectricityTicket p = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
		PGPKeyGen.signTicketForNewUser(p,id1,pass1);
		PGPKeyGen.signTicketForNewUser(p,id2,pass2);
		PGPKeyGen.signTicketForNewUser(p,id3,pass3);
		PGPKeyGen.signTicketForNewUser(p,id4,pass4);
		
		Boolean ret =  PGPKeyGen.verifyTicket(p);
		return ret;
	}

}
