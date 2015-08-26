package uk.ac.imperial.smartmeter.tests.crypto;

import uk.ac.imperial.smartmeter.tests.GenericTester;

public class CryptoTester extends GenericTester {

	@Override
	public Integer main() {
		testLog.add(new TestBasicSignUnsign());
		testLog.add(new TestTicketVerification());
		testLog.add(new TestTicketVerificationMultipleSignatories());
		return reportLog();
	}
	public static void main(String[]args){
	CryptoTester a = new CryptoTester();
	a.main();
	}
}
