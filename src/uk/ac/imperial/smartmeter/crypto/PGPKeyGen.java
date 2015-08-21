package uk.ac.imperial.smartmeter.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import uk.ac.imperial.smartmeter.res.Twople;


public class PGPKeyGen {
	public static final char delimChar = '_';
	public static final byte delim = (byte)delimChar;
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
	public static ArrayList<Twople<String, Byte[]>> deCatSignatures(String file)
	{
		//for the purposes of this exercise, it is assumed that _ is a suitable separating character
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	public static void main(String[] args) throws Exception
	{
		//so, cat two files together? fmt: user, levels,  sig, othersigs, ticket.
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
        
        System.out.println(catSignatures("signme.txt"));
        System.out.println(deCatSignatures("cat.txt"));
        System.out.println(ret?"yay":"no");
		//PGPSigner.verifyString(signme, "pub.bpg");
        //PGPSigner.main(new String[]{"-s", "signme.txt", "secret.bpg", "jackson"});
		//PGPSigner.main(new String[]{"-v", "signme.txt.bpg", "pub.bpg"});
		
	}
}
