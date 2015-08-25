package uk.ac.imperial.smartmeter.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

/**
 * A simple utility class that signs and verifies files.
 * <p>
 * To sign a file: SignedFileProcessor -s [-a] fileName secretKey passPhrase.<br>
 * If -a is specified the output file will be "ascii-armored".
 * <p>
 * To decrypt: SignedFileProcessor -v fileName publicKeyFile.
 * <p>
 * <b>Note</b>: this example will silently overwrite files, nor does it pay any attention to
 * the specification of "_CONSOLE" in the filename. It also expects that a single pass phrase
 * will have been used.
 * <p>
 * <b>Note</b>: the example also makes use of PGP compression. If you are having difficulty getting it
 * to interoperate with other PGP programs try removing the use of compression first.
 */
public class PGPSigner
{
    /*
     * verify the passed in file as being correctly signed.
     */
	
	public static void verifyFileAndDisplay(
	        InputStream        in,
	        InputStream        keyIn)
	        throws Exception
	        {
		 if (verifyFile(in, keyIn))
	        {
	            System.out.println("signature verified.");
	        }
	        else
	        {
	            System.out.println("signature verification failed.");
	        }
	}
	
	public static Boolean verifyFile(String inStr, String keyInStr)
	{
        try {

    		FileInputStream    in = new FileInputStream(inStr);
			FileInputStream    keyIn = new FileInputStream(keyInStr);
			
			return verifyFile(in,keyIn);
		} catch (Exception e) {
			return false;
		}
        
        
	}
	public static String signString(
		  String          input,
	      String keyLoc,
	      //  OutputStream    out,
	        String         pass)
	        throws IOException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException
	    {
		//not deprecated, just fundamentally broken.
		  ByteArrayOutputStream out = new ByteArrayOutputStream();
		  FileInputStream keyIn = new FileInputStream(keyLoc);

	        PGPSecretKey                pgpSec = readSecretKey(keyIn);
	        PGPPrivateKey               pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass.toCharArray()));
	        PGPSignatureGenerator       sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
	        
	        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
	        
	        @SuppressWarnings("unchecked")
			Iterator<String>    it = pgpSec.getPublicKey().getUserIDs();
	        if (it.hasNext())
	        {
	            PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
	            
	            spGen.setSignerUserID(false, it.next());
	            sGen.setHashedSubpackets(spGen.generate());
	        }
	        
	        PGPCompressedDataGenerator  cGen = new PGPCompressedDataGenerator(
	                                                                PGPCompressedData.ZLIB);
	        
	        BCPGOutputStream            bOut = new BCPGOutputStream(cGen.open(out));
	        
	        sGen.generateOnePassVersion(false).encode(bOut);
	        
	        //File                        file = new File("dummy");
	        PGPLiteralDataGenerator     lGen = new PGPLiteralDataGenerator();
	        OutputStream                lOut = lGen.open(out, PGPLiteralData.BINARY, "Steven", new Date(), new byte[256]);
	        //FileInputStream             fIn = new FileInputStream(file);
	        
	        for (byte b : input.getBytes())
	        {

	            lOut.write(b);
	            sGen.update((byte)b);
	        	
	        }

	        lGen.close();
	        //fIn.close();
	        sGen.generate().encode(bOut);

	        cGen.close();
	        FileOutputStream t = new FileOutputStream("test.txt");

	        String x = new String(out.toByteArray());
	        
	        for (byte b : out.toByteArray())
	        {

	        	t.write(b);
	        	
	        }
	        t.close();
	        out.close();
	        return x;
	    
	}
	public static Boolean verifyString(
			String in,
			String keyIn
			)
		    throws Exception
	{
		
		return verify(new JcaPGPObjectFactory(in.getBytes(Charset.forName("UTF-8"))), new FileInputStream(keyIn));
	}
	public static Boolean verifyString(
			String in,
			InputStream keyIn
			)
		    throws Exception
	{
		
		return verify(new JcaPGPObjectFactory(in.getBytes(Charset.forName("UTF-8"))), keyIn);
	}
	public static Boolean verifyFile(
	        InputStream        in,
	        InputStream        keyIn)
	        throws Exception
	    {
	    in = PGPUtil.getDecoderStream(in);
	    return verify(new JcaPGPObjectFactory(in),keyIn);
	    }
	public static Boolean verify(
			JcaPGPObjectFactory pgpFact,
			InputStream keyIn)
			throws Exception
	{
	        

	        PGPCompressedData           c1 = (PGPCompressedData)pgpFact.nextObject();

	        pgpFact = new JcaPGPObjectFactory(c1.getDataStream());
	            
	        PGPOnePassSignatureList     p1 = (PGPOnePassSignatureList)pgpFact.nextObject();
	            
	        PGPOnePassSignature         ops = p1.get(0);
	            
	        PGPLiteralData              p2 = (PGPLiteralData)pgpFact.nextObject();

	        InputStream                 dIn = p2.getInputStream();
	        int                         ch;
	        PGPPublicKeyRingCollection  pgpRing = new PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());

	        PGPPublicKey                key = pgpRing.getPublicKey(ops.getKeyID());
	        FileOutputStream            out = new FileOutputStream(p2.getFileName());

	        ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), key);
	        while ((ch = dIn.read()) >= 0)
	        {
	            ops.update((byte)ch);
	            out.write(ch);
	        }

	        out.close();
	        
	        PGPSignatureList            p3 = (PGPSignatureList)pgpFact.nextObject();

	        return ops.verify(p3.get(0));
	}
    

    /**
     * Generate an encapsulated signed file.
     * 
     * @param fileName
     * @param keyIn
     * @param out
     * @param pass
     * @param armor
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws PGPException
     * @throws SignatureException
     */
	static public void signFile(String file, String keyIn, String pass) throws IOException, NoSuchAlgorithmException,
			NoSuchProviderException, PGPException, SignatureException {
		FileInputStream     keyStream = new FileInputStream(keyIn);
        FileOutputStream    outStream = new FileOutputStream(file+".bpg");
        
        signFile(file, keyStream, outStream, pass.toCharArray(),true);
	}
	static public void signFile(
	        String          fileName,
	        InputStream     keyIn,
	        OutputStream    out,
	        char[]          pass,
	        boolean         armor)
	        throws IOException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, SignatureException
	    {
	        if (armor)
	        {
	            out = new ArmoredOutputStream(out);
	        }

	        PGPSecretKey                pgpSec = readSecretKey(keyIn);
	        PGPPrivateKey               pgpPrivKey = pgpSec.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
	        PGPSignatureGenerator       sGen = new PGPSignatureGenerator(new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));
	        
	        sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
	        
	        @SuppressWarnings("unchecked")
			Iterator<String>    it = pgpSec.getPublicKey().getUserIDs();
	        if (it.hasNext())
	        {
	            PGPSignatureSubpacketGenerator  spGen = new PGPSignatureSubpacketGenerator();
	            
	            spGen.setSignerUserID(false, it.next());
	            sGen.setHashedSubpackets(spGen.generate());
	        }
	        
	        PGPCompressedDataGenerator  cGen = new PGPCompressedDataGenerator(
	                                                                PGPCompressedData.ZLIB);
	        
	        BCPGOutputStream            bOut = new BCPGOutputStream(cGen.open(out));
	        
	        sGen.generateOnePassVersion(false).encode(bOut);
	        
	        File                        file = new File(fileName);
	        PGPLiteralDataGenerator     lGen = new PGPLiteralDataGenerator();
	        OutputStream                lOut = lGen.open(bOut, PGPLiteralData.BINARY, file);
	        FileInputStream             fIn = new FileInputStream(file);
	        int                         ch;
	        
	        while ((ch = fIn.read()) >= 0)
	        {
	            lOut.write(ch);
	            sGen.update((byte)ch);
	        }

	        lGen.close();
	        fIn.close();
	        sGen.generate().encode(bOut);

	        cGen.close();

	        if (armor)
	        {
	            out.close();
	        }
	    }

    public static void main(
        String[] args)
        throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        if (args[0].equals("-s"))
        {
            if (args[1].equals("-a"))
            {
                FileInputStream     keyIn = new FileInputStream(args[3]);
                FileOutputStream    out = new FileOutputStream(args[2] + ".asc");
                
                signFile(args[2], keyIn, out, args[4].toCharArray(), true);
            }
            else
            {
                FileInputStream     keyIn = new FileInputStream(args[2]);
                FileOutputStream    out = new FileOutputStream(args[1] + ".bpg");
                
                signFile(args[1], keyIn, out, args[3].toCharArray(), false);
            }
        }
        else if (args[0].equals("-v"))
        {
            FileInputStream    in = new FileInputStream(args[1]);
            FileInputStream    keyIn = new FileInputStream(args[2]);
            
            System.out.println(verifyFile(in, keyIn) ? "yay" : "boo");
        }
        else
        {
            System.err.println("usage: SignedFileProcessor -v|-s [-a] file keyfile [passPhrase]");
        }
    }
    
    static PGPSecretKey readSecretKey(String fileName) throws IOException, PGPException
    {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
        PGPSecretKey secKey = readSecretKey(keyIn);
        keyIn.close();
        return secKey;
    }

    /**
     * A simple routine that opens a key ring file and loads the first available key
     * suitable for signature generation.
     * 
     * @param input stream to read the secret key ring collection from.
     * @return a secret key.
     * @throws IOException on a problem with using the input stream.
     * @throws PGPException if there is an issue parsing the input stream.
     */
    static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException
    {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
            PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //

        Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext())
        {
            PGPSecretKeyRing keyRing = (PGPSecretKeyRing)keyRingIter.next();

            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext())
            {
                PGPSecretKey key = (PGPSecretKey)keyIter.next();

                if (key.isSigningKey())
                {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }
}