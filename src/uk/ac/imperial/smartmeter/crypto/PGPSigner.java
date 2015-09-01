package uk.ac.imperial.smartmeter.crypto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
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
 * To sign a file: PGPSigner -s [-a] fileName secretKey passPhrase.<br>
 * If -a is specified the output file will be "ascii-armored".
 * <p>
 * To decrypt: PGPSigner -v fileName publicKeyFile.
 * <p>
 */
public class PGPSigner
{
	/**
	 * Verifies that the file that has been passed in has been correctly signed by the owner of the public key that has been passed in.
	 *  restoring the original document if possible.
	 * Writes the success? to the console.
	 * 
	 * @param in An inputstream pointing to the location of the file to be verified.
	 * @param keyIn An inputstream pointing to the location of the file holding the public key of the presumed signatory.
	 * @throws Exception
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
	/**
	 * Verifies that the file at the location pointed to by the inStr parameter has been correctly signed by the owner of the public key pointed to be the keyInStr parameter.
	 * The original file (pre-signature) is restored to its original location.
	 * @param inStr A string pointing to the location of the file to be verified.
	 * @param keyInStr A string pointing to the location of the file holding the public key of the presumed signatory.
	 * @return Success?
	 */
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
	/**
	 * Verifies that the file that has been passed in has been correctly signed by the owner of the public key that has been passed in.
	 *  restoring the original document if possible.
	 * 
	 * @param in An inputstream pointing to the location of the file to be verified.
	 * @param keyIn An inputstream pointing to the location of the file holding the public key of the presumed signatory.
	 * @param in
	 * @param keyIn
	 * @return
	 * @throws Exception
	 */
	public static Boolean verifyFile(
	        InputStream        in,
	        InputStream        keyIn)
	        throws Exception
	    {
	    in = PGPUtil.getDecoderStream(in);
	    Boolean ret = verify(new JcaPGPObjectFactory(in),keyIn);
	    in.close();
	    keyIn.close();
	    return ret;
	    }
	/**
	 * Verifies that the file that has been passed in has been correctly signed by the owner of the public key that has been passed in.
	 *  restoring the original document if possible.
	 * Used internally.
	 * 
	 * @param pgpFact An object factory derived from the location of the file to be verified.
	 * @param keyIn An inputstream pointing to the location of the file holding the public key of the presumed signatory.
	 * @return Success?
	 * @throws Exception
	 */
	private static Boolean verify(
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
	        keyIn.close();
	        dIn.close();
	        
	        PGPSignatureList            p3 = (PGPSignatureList)pgpFact.nextObject();

	        return ops.verify(p3.get(0));
	}
    

	/**
	 * Generate an encapsulated signed file. The original message is contained within the signature in an encrypted fashion.
	 * The signed file has a filename equal to original filename +.bpg.
	 * 
	 * @param file The filename of the file to be signed.
	 * @param keyIn The filename of the file holding the secret key that shall be used in the signing.
	 * @param pass  The passphrase used to open the secret lock and reveal the private key, to be used in the signing process.
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
	/**
	 * Generate an encapsulated signed file. The original message is contained within the signature in an encrypted fashion.
	 * The signed file has a filename equal to original filename +.bpg.
	 * 
	 * @param fileName The filename of the file to be signed.
	 * @param keyIn The filename of the file holding the secret key that shall be used in the signing.
	 * @param out An outputstream which the signed file shall be written to.
	 * @param pass  The passphrase used to open the secret lock and reveal the private key, to be used in the signing process.
	 * @param armor If true, wraps the output in an ArmoredOutputStream for ASCII armouring.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws PGPException
	 * @throws SignatureException
	 */
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
/**
 * Main function allows for command line signing and verification of files.
 * SignedFileProcessor -v|-s [-a] file keyfile [passPhrase]
 * @param args The mode, optional armouring, keyfile location, and passphrase.
 * @throws Exception
 */
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
    
    /**
     * Reads the first secret key from a file containing secret keys.
     * @param fileName The location of the secret ring collection.
     * @return A secret key.
     * @throws IOException
     * @throws PGPException
     */
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