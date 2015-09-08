package uk.ac.imperial.smartmeter.tests.crypto;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Pair;
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
		
		Pair<String, String> y1 = KeyPairGen.genKeySet(id1, pass1);
		SignatureHelper.printPubKey(id1,y1.right);
		SignatureHelper.printSecKey(id1,y1.left);
		Pair<String, String> y2 = KeyPairGen.genKeySet(id2, pass2);
		SignatureHelper.printPubKey(id2,y2.right);
		SignatureHelper.printSecKey(id2,y2.left);
		Pair<String, String> y3 = KeyPairGen.genKeySet(id3, pass3);
		SignatureHelper.printPubKey(id3,y3.right);
		SignatureHelper.printSecKey(id3,y3.left);
		Pair<String, String> y4 = KeyPairGen.genKeySet(id4, pass4);
		SignatureHelper.printPubKey(id4,y4.right);
		SignatureHelper.printSecKey(id4,y4.left);
		
		
		ElectricityTicket p = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
		SignatureHelper.signTicketForNewUser(p,id1,pass1);
		SignatureHelper.signTicketForNewUser(p,id2,pass2);
		SignatureHelper.signTicketForNewUser(p,id3,pass3);
		SignatureHelper.signTicketForNewUser(p,id4,pass4);
		
		Boolean ret =  SignatureHelper.verifyTicket(p);
		return ret;
	}

}
