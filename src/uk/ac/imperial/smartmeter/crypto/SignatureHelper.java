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

import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Quadruple;
import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;


public class SignatureHelper {
	public static final char delimChar = '_';
	public static final byte delim = (byte)delimChar;
	
	public static Byte[] getClassyByteArray(byte[] b)
	{

		Byte[] Bytes = new Byte[b.length];

	    for(int i = 0; i < b.length; i++) {
	        Bytes[i] = b[i];
	    }
	    return Bytes;
	}
	public static byte[] getPrimitiveByteArray(Byte[] b)
	{
		byte[] bytes = new byte[b.length];

	    for(int i = 0; i < b.length; i++) {
	        bytes[i] = b[i];
	    }
	    return bytes;
	}
	public static String signTicketForNewUser(ElectricityTicket tkt, String userId, String password)
	{
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			String userPubKey = "geronimo";//getPublicKey(newUserId);

			md.update(userPubKey.getBytes("UTF-8")); // Change this to "UTF-16" if needed
			byte[] digest = md.digest();
			
			
			FileOutputStream ticketOut = new FileOutputStream(tkt.getId()+".txt",false);
			
			
			md = MessageDigest.getInstance("SHA-256");
			md.update(tkt.toString().getBytes("UTF-8"));
			md.update((byte)'_');
			md.update(digest);
			
			ticketOut.write(md.digest());
			
			
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
			return "";
		} catch (NoSuchAlgorithmException | IOException | NoSuchProviderException | SignatureException | PGPException e) {
			return "";
		}
	}
	public static String stringFromArrayList(ArrayList<Byte> b)
	{
		return new String(getPrimitiveByteArray(b.toArray(new Byte[b.size()])));
	}
	public static Boolean verifyTicket(ElectricityTicket t) {

		Boolean ret = true;
		try {
			if(t.getNSignatures()<=0)
			{
			 return false;
			}
			for (int i = 0; i < t.getNSignatures(); i++) {
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
				
				ret &= vIn.equals(tIn)&&!vIn.isEmpty();
				
			}
		} catch (IOException e) { return false;
		}

		return ret;

	}
	public static Boolean verifyTicket(ElectricityTicket t, UserAddressBook book) {
		for (Quadruple<String, Date, ElectricityTicket, byte[]> x : t.getSignatures())
		{
		book.findAndPrintPubKey(x.left);
		}
		
		return verifyTicket(t);
	}
	public static void main(String[] args) throws Exception
	{
		
		
	}
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
