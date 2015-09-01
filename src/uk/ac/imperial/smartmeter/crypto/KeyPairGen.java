package uk.ac.imperial.smartmeter.crypto;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import uk.ac.imperial.smartmeter.res.Twople;

/**
 * A simple utility class that generates a RSA PGPPublicKey/PGPSecretKey pair.
 * <p>
 * usage: KeyPairGen [-a] identity passPhrase
 * <p>
 * Where identity is the name to be associated with the public key. The keys are placed 
 * in the files pub.[asc|bpg] and secret.[asc|bpg].
 */
public class KeyPairGen
{
	/**
	 * Creates a public/secret key pair for a specified identity and passphrase, putting the outputs in specified OutputStream objects.
	 * 
	 * @param secretOut The stream which the secret key is output to.
	 * @param publicOut The stream which the public key is output to.
	 * @param pair      KeyPair object that defines the encryption protocol to be used.
	 * @param identity  The identity of the user which the keys will be bound to.
	 * @param passPhrase The passphrase that will be used to access the secret key and sign documents.
	 * @param armor  If true, wraps the output in an ArmoredOutputStream.
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @throws PGPException
	 */
    private static void exportKeyPair(
        OutputStream    secretOut,
        OutputStream    publicOut,
        KeyPair         pair,
        String          identity,
        char[]          passPhrase,
        boolean         armor)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {    
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
        PGPKeyPair          keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, pair, new Date());
        PGPSecretKey        secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, identity, sha1Calc, null, null, new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc).setProvider("BC").build(passPhrase));
        
        secretKey.encode(secretOut);
        
        secretOut.close();
        
        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey    key = secretKey.getPublicKey();
        
        key.encode(publicOut);
        
        publicOut.close();
    }

    /**
     * Generates a public/secret key pair wrapped in a Twople object from a given id and password.
     * @param userId The identity to be associated with the user and bound to the key pair.
     * @param pass The password that can be used to access the secret key.
     * @return A Twople<String,String> containing the secretkey on the left side, and the public key on the right.
     */
	public static Twople<String, String> genKeySet(String userId, String pass) {

		Twople<String, String> ret = new Twople<String, String>();
		try {
			Security.addProvider(new BouncyCastleProvider());

			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");

			kpg.initialize(1024);
			
			KeyPair kp = kpg.generateKeyPair();
			ByteArrayOutputStream out3 = new ByteArrayOutputStream();
			ByteArrayOutputStream out4 = new ByteArrayOutputStream();
			exportKeyPair(out3, out4, kp, userId, pass.toCharArray(), true);
			ret.left = new String(out3.toByteArray());
			ret.right = new String(out4.toByteArray());
		} catch (Exception e) {

		}
		return ret;
	}
	/**
	 * Generates and writes several public/secret key pairs to files based on a list of identities and passwords.
	 * 
	 * @param identities An array of Twoples containing username and passwords to be associated with public/secret key pairs.
	 */
    public static void genKeySet(ArrayList<Twople<String, String>> identities)
    {
    	try{
    		Security.addProvider(new BouncyCastleProvider());

            KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
            
            kpg.initialize(1024);
            
            KeyPair                    kp = kpg.generateKeyPair();
            
                for (Twople<String, String> t : identities)
                {
                    FileOutputStream    out3 = new FileOutputStream(t.left+"_secret.bpg");
                    FileOutputStream    out4 = new FileOutputStream(t.left+"_pub.bpg");

                    exportKeyPair(out3, out4, kp, t.left, t.right.toCharArray(), true); //not the same key pair as the previous export
                }

    	}
    	catch (Exception e)
    	{
    		
    	}
    }
    /**
     * Outputs to file a public/secret key pair based on the input.
     * Used as: KeyPairGen [-a] identity passPhrase
     * 
     * @param args The identity and passphrase to be associated with the new public and private keys.
     * @throws Exception
     */
    public static void main(
        String[] args)
        throws Exception
    {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
        
        kpg.initialize(1024);
        
        KeyPair                    kp = kpg.generateKeyPair();
        
        if (args.length < 2)
        {
            System.out.println("KeyPairGen [-a] identity passPhrase");
            System.exit(0);
        }
        
        if (args[0].equals("-a"))
        {
            if (args.length < 3)
            {
                System.out.println("KeyPairGen [-a] identity passPhrase");
                System.exit(0);
            }
            FileOutputStream    out1 = new FileOutputStream("secret.asc");
            FileOutputStream    out2 = new FileOutputStream("pub.asc");
            
            exportKeyPair(out1, out2, kp, args[1], args[2].toCharArray(), true);
            
            String pub = out1.toString();
            String priv = out2.toString();
            pub.equals(priv);
            
        }
        else
        {
            FileOutputStream    out1 = new FileOutputStream("secret.bpg");
            FileOutputStream    out2 = new FileOutputStream("pub.bpg");
            
            exportKeyPair(out1, out2, kp, args[0], args[1].toCharArray(), false);
        }
    }
}
