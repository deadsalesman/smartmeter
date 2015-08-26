package uk.ac.imperial.smartmeter.tests.crypto;

import java.util.Date;
import java.util.UUID;

import uk.ac.imperial.smartmeter.crypto.KeyPairGen;
import uk.ac.imperial.smartmeter.crypto.SignatureHelper;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.tests.GenericTest;

public class TestTicketVerification extends GenericTest {

	@Override
	public boolean doTest() throws Exception {
		String id = "ticketverification";
		String pass = "tktpass";
		
		Twople<String, String> y = KeyPairGen.genKeySet(id, pass);
		SignatureHelper.printPubKey(id,y.right);
		SignatureHelper.printSecKey(id,y.left);
		ElectricityTicket p = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
		SignatureHelper.signTicketForNewUser(p,id,pass);
		Boolean ret =  SignatureHelper.verifyTicket(p);
		return ret;
	}

}
