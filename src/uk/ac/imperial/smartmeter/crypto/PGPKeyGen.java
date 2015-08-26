package uk.ac.imperial.smartmeter.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;

import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.res.Quadruple;
import uk.ac.imperial.smartmeter.res.Twople;
import uk.ac.imperial.smartmeter.webcomms.UserAddressBook;


public class PGPKeyGen {
	public static final char delimChar = '_';
	public static final byte delim = (byte)delimChar;
	PGPKeyGen()
	{
		try {
			ArrayList<Twople<String, String>> x = new ArrayList<Twople<String, String>>();
			x.add(new Twople<String, String>("ada","lovelace"));

			x.add(new Twople<String, String>("michael","jackson"));
			KeyPairGen.genKeySet(x);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//DEPRECATED:
	public static ArrayList<Twople<String, Byte[]>> deCatSignatures(String file)
	{
	/*	//for the purposes of this exercise, it is assumed that _ is a suitable separating character
		ArrayList<Twople<String, Byte[]>> x = new ArrayList<Twople<String, Byte[]>>();
		Byte temp = 0;
		try {

			FileInputStream in  = new FileInputStream(file);
			ArrayList<Byte> data = new ArrayList<Byte>();
			Boolean ticket = true;
			Boolean firstPart = true;
			String id = "";
			while ((temp=Byte.valueOf((byte) in.read()))>=0)
				{
					if (temp.equals(delim))
					{
						if (ticket)
						{
							ticket = false;
							x.add(new Twople<String, Byte[]>("Ticket:",data.toArray(new Byte[data.size()])));
						}
						else
						{
							if (firstPart)
							{

								firstPart = false;
								id = new String(getPrimitiveByteArray(data.toArray(new Byte[data.size()])));
							}
							else
							{
								firstPart = true;
								x.add(new Twople<String, Byte[]>(id,data.toArray(new Byte[data.size()])));
							}
						}

						data = new ArrayList<Byte>();
					}
					else
					{
						data.add(temp);
					}
				}

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return x;
		*/
		return null;
	}
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
	public static String catSignatures(String baseid)
	{
		String ret = "";
		ArrayList<Byte> data = new ArrayList<Byte>();
		ArrayList<Byte[]> files = new ArrayList<Byte[]>();
		Byte temp = 0;
		String file = baseid;
		try {
			FileInputStream in  = new FileInputStream(baseid);
			while ((temp=Byte.valueOf((byte) in.read()))>=0)
				{
					data.add(temp);
					ret += temp;
				}
			in.close();

			 data.add(delim);
			 Byte[] curr = data.toArray(new Byte[data.size()]);
			 files.add(curr);
			try{
			 while(true)
			 {
				 file += ".bpg";
				 data = new ArrayList<Byte>();
				 FileInputStream signatureInput  = new FileInputStream(file);
				   while ((temp=Byte.valueOf((byte) signatureInput.read()))>=0)
						{
							data.add(temp);
							ret += temp;
						}
				   signatureInput.close();

					 data.add(delim);
					 Byte[] current = data.toArray(new Byte[data.size()]);
					 files.add(getClassyByteArray("testID_".getBytes()));
					 files.add(current);
			 
			}
			}catch(IOException e)
			{
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileOutputStream os = new FileOutputStream("cat.txt");
			for (Byte[] b : files)
			{
				
			    os.write(getPrimitiveByteArray(b));
			}
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
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
		//so, cat two files together? fmt: user, levels,  sig, othersigs, ticket.
		PGPKeyGen x = new PGPKeyGen();
		Twople<String, String> y = KeyPairGen.genKeySet("mich", "jack");
		printPubKey("mich",y.right);
		printSecKey("mich",y.left);
        Security.addProvider(new BouncyCastleProvider());
		//String signme = "billie jean is not my lover";
		
   //     PGPSigner.signFile("signme.txt", "michael_secret.bpg", "jackson");
  //      PGPSigner.signFile("signme.txt.bpg", "ada_secret.bpg", "lovelace");
   //     Boolean ret = PGPSigner.verifyFile("signme.txt.bpg.bpg", "ada_pub.bpg");
        
        //String signed = PGPSigner.signString("workcurseyou", "ada_secret.bpg", "lovelace");
        //System.out.println(PGPSigner.verifyString(signed, "ada_pub.bpg"));
        
        ElectricityTicket p = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
	    int deph = 1;
	    for (int i = 0; i < deph; i++)
	    {
	    	signTicketForNewUser(p,"mich","jack");
	    	p.magnitude += 2;
	    }
	    ;
	    System.out.println(verifyTicket(p)?"yay":"no");
        ElectricityTicket t = new ElectricityTicket(new Date(), new Date(), 0., UUID.randomUUID().toString(), UUID.randomUUID().toString());
	    int depth = 13;
	    for (int i = 0; i < depth; i++)
	    {
	    	signTicketForNewUser(t,"michael","jackson");
	    	t.magnitude += 2;
	    }
	    System.out.println(verifyTicket(t)?"yay":"no");
		
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
