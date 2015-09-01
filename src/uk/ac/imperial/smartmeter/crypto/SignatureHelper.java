package uk.ac.imperial.smartmeter.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;

import org.bouncycastle.openpgp.PGPException;

import uk.ac.imperial.smartmeter.allocator.TicketAllocator;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Quadruple;
import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;

/**
 * Utility class which aids in the signing of ElectricityTickets.
 * 
 * @author bwindo
 * @see TicketAllocator
 */
public class SignatureHelper {
	public static final char delimChar = '_';
	public static final byte delim = (byte)delimChar;
	
	/**
	 * Returns a Byte[] representation of a given byte[]
	 * @param b The byte array.
	 * @return The Byte array.
	 */
	public static Byte[] getClassyByteArray(byte[] b)
	{

		Byte[] Bytes = new Byte[b.length];

	    for(int i = 0; i < b.length; i++) {
	        Bytes[i] = b[i];
	    }
	    return Bytes;
	}
	/**
	 * Returns a byte[] representation of a given Byte[]
	 * @param b The Byte array.
	 * @return The byte array.
	 */
	public static byte[] getPrimitiveByteArray(Byte[] b)
	{
		byte[] bytes = new byte[b.length];

	    for(int i = 0; i < b.length; i++) {
	        bytes[i] = b[i];
	    }
	    return bytes;
	}
	/**
	 * Adds a signature to an electricity ticket based on its current values and some properties of the signing agent.
	 * 
	 * @param tkt The ElectricityTicket to be signed.
	 * @param userId The userId of the agent to sign the ticket. This determines which keypair is used, and what is logged in the ticket.
	 * @param password The password of the agent desiring to sign the ticket. If this is incorrect, the ticket will not be signed.
	 * @return Success?
	 */
	public static Boolean signTicketForNewUser(ElectricityTicket tkt, String userId, String password)
	{
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String userPubKey = "geronimo";//getPublicKey(newUserId);

			md.update(userPubKey.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] digest = md.digest();
			
			
			FileOutputStream ticketOut = new FileOutputStream(tkt.getId()+".txt",false);
			
			//Create a hashed version of the current ticket. 
			md = MessageDigest.getInstance("SHA-256");
			md.update(tkt.toString().getBytes("UTF-8"));
			md.update((byte)'_');
			md.update(digest);
			
			ticketOut.write(md.digest());
			
			//Sign the hashed ticket. A hash is required so that each signature increases the size of the ticket by a constant value
			//rather than a value proportional to the size of the ticket.
			PGPSigner.signFile(tkt.getId() + ".txt", userId +"_secret.bpg", password);
			
			ticketOut.close();
			

			FileInputStream ticketIn = new FileInputStream(tkt.getId()+".txt");
			
			FileInputStream sigIn = new FileInputStream(tkt.getId()+".txt.bpg");
			
			byte temp;
			ArrayList<Byte> b = new ArrayList<Byte>();
			while ((temp=(byte) sigIn.read())>=0)
			{
				b.add(temp);
			}
			String sig = stringFromArrayList(b);
			tkt.addSignature(userId, sig);
	
			ticketIn.close();
			sigIn.close();
			return true;
		} catch (NoSuchAlgorithmException | IOException | NoSuchProviderException | SignatureException | PGPException e) {
			return false;
		}
	}
	/**
	 * Returns a String from an arraylist of Bytes. By default, this method only exists for arrays of bytes.
	 * @param b The ArrayList of Bytes.
	 * @return The String representation of b
	 */
	public static String stringFromArrayList(ArrayList<Byte> b)
	{
		return new String(getPrimitiveByteArray(b.toArray(new Byte[b.size()])));
	}
    /**
     * Iteratively verifies each layer of the ticket to ensure authenticated transactions have occurred at every layer of the system.
     * 
     * @param t The ElectricityTicket to be verified.
     * @return Success?
     */
	public static Boolean verifyTicket(ElectricityTicket t) {

		Boolean ret = true;
		try {
			//If the ticket has never been signed, it is broken. All tickets should at least be signed by the central ticket allocator.
			if(t.getNSignatures()<=0)
			{
			 return false;
			}
			for (int i = 0; i < t.getNSignatures(); i++) {
				//Write both the current signature and the hash of all levels of the ticket 'below' that signature. 
				//This hash should be the same as the content of the decrypted signature, if it is not then the ticket has been altered.
				t.writeLog(i);
				Boolean check =PGPSigner.verifyFile(t.getId() + ".s", t.getSignature(i).left + "_pub.bpg");
				ret &= check;
				FileInputStream tktIn = new FileInputStream(t.getId() + ".v");
				FileInputStream verifIn = new FileInputStream(t.getId() + ".txt");
				
				byte temp;
				ArrayList<Byte> b = new ArrayList<Byte>();
				while((temp=(byte)tktIn.read())!=-1){b.add(temp);}
				String tIn = stringFromArrayList(b);
				
				b = new ArrayList<Byte>();
				while((temp=(byte)verifIn.read())!=-1){b.add(temp);}
				String vIn = stringFromArrayList(b);
				
				verifIn.close();
				tktIn.close();
				//Verify the calculated hash is identical to the stored hash and that both are nonzero.
				ret &= vIn.equals(tIn)&&!vIn.isEmpty();
				
			}
		} catch (IOException e) { return false;
		}

		return ret;

	}
	/**
	 * Iteratively verifies each layer of the ticket to ensure authenticated transactions have occurred at every layer of the system.
	 * As this requires public keys to be locally present for each of the signatories, it prints all these public keys that are locally stored. 
	 * This is objectively awful but the performance hit isn't too bad while n is very, very small. 
	 * 
	 * @param t The ticket to be signed.
	 * @param book The information on all users that may have signed the ticket.
	 * @return
	 */
	public static Boolean verifyTicket(ElectricityTicket t, UserAddressBook book) {
		for (Quadruple<String, Date, ElectricityTicket, byte[]> x : t.getSignatures())
		{
		book.findAndPrintPubKey(x.left);
		}
		
		return verifyTicket(t);
	}
	/**
	 * Writes a public key to its associated output file.
	 * 
	 * @param id The identity of the owner of the public key.
	 * @param key The public key to be printed.
	 */
	public static void printPubKey(String id, String key) {
		try {
			FileOutputStream fOut = new FileOutputStream(id+"_pub.bpg");
			for (byte b: key.getBytes("UTF-8"))
			{
				fOut.write(b);
			}
			fOut.close();
		} catch (IOException e) {
		}
	}
	/**
	 * Writes a secret key to its associated output file.
	 * 
	 * @param id The identity of the owner of the secret key.
	 * @param key The secret key to be printed.
	 */
	public static void printSecKey(String id, String key) {
		try {
			FileOutputStream fOut = new FileOutputStream(id+"_secret.bpg");
			for (byte b: key.getBytes("UTF-8"))
			{
				fOut.write(b);
			}
			fOut.close();
		} catch (IOException e) {
		}
	}
}
