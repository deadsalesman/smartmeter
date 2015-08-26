package uk.ac.imperial.smartmeter.tests.crypto;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.PGPKeyGen;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestTicketVerification extends GenericTest {

	@Override
	public boolean doTest() throws Exception {
		String id = "ticketverification";
		String pass = "tktpass";
		
		Twople<String, String> y = KeyPairGen.genKeySet(id, pass);
		PGPKeyGen.printPubKey(id,y.right);
		PGPKeyGen.printSecKey(id,y.left);
		ElectricityTicket p = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
		PGPKeyGen.signTicketForNewUser(p,id,pass);
		Boolean ret =  PGPKeyGen.verifyTicket(p);
		return ret;
	}

}
