package uk.ac.imperial.smartmeter.crypto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
// Bouncy castle imports
//
public class PGPHelper {
      private static File publicKeyFile = new File("/Development/Java/Single Sign On with Encryption(PGP)/PGP1D0.pkr");
      private static File privateKeyFile = new File("/Development/Java/Single Sign On with Encryption(PGP)/PGP1D0.skr");
      private static String privateKeyPassword = "passphrase";
 
       //
      // Public class method decrypt
      //
      public static String decrypt(byte[] encdata) {
              System.out.println("decrypt(): data length=" + encdata.length);
              // ----- Decrypt the file
              try {
                      ByteArrayInputStream bais = new ByteArrayInputStream(encdata);
                      FileInputStream privKey = new FileInputStream(privateKeyFile);
                      return _decrypt(bais, privKey, privateKeyPassword.toCharArray());
              } catch (Exception e) {
                      System.out.println(e.getMessage());
                      e.printStackTrace();
              }
              return null;
      }
      //
      // Public class method encrypt
      //
      public static byte[] encrypt(byte[] data) {
              try
              {
                      // ----- Read in the public key
                      PGPPublicKey key = readPublicKeyFromCol(new FileInputStream(publicKeyFile));
                      System.out.println("Creating a temp file...");
                      // create a file and write the string to it
                      File tempfile = File.createTempFile("pgp", null);
                      FileOutputStream fos = new FileOutputStream(tempfile);
                      fos.write(data);
                      fos.close();
                      System.out.println("Temp file created at ");
                      System.out.println(tempfile.getAbsolutePath());
                      System.out.println("Reading the temp file to make sure that the bits were written\n--------------");
                      BufferedReader isr = new BufferedReader(new FileReader(tempfile));
                      String line = "";
                      while ( (line = isr.readLine())!= null )
                      {
                              System.out.println(line + "\n");
                      }
                      // find out a little about the keys in the public key ring
                      System.out.println("Key Strength = " + key.getBitStrength());
                      System.out.println("Algorithm = " + key.getAlgorithm());
                      System.out.println("Bit strength = " + key.getBitStrength());
                      System.out.println("Version = " + key.getVersion());
                      System.out.println("Encryption key = " + key.isEncryptionKey()+ ", Master key = " + key.isMasterKey());
                      int count = 0;
                      for ( java.util.Iterator iterator = key.getUserIDs(); iterator.hasNext(); )
                      {
                              count++;
                              System.out.println((String) iterator.next());
                      }
                      System.out.println("Key Count = " + count);
                      // create an armored ascii file
                      // FileOutputStream out = new FileOutputStream(outputfile);
                      // encrypt the file
                      // encryptFile(tempfile.getAbsolutePath(), out, key);
                      // Encrypt the data
                      ByteArrayOutputStream baos = new ByteArrayOutputStream();
                      _encrypt(tempfile.getAbsolutePath(), baos, key);
                      System.out.println("encrypted text length=" + baos.size());			
                      tempfile.delete();
                      return baos.toByteArray();
              }
              catch (PGPException e)
              {
                      // System.out.println(e.toString());
                      System.out.println(e.getUnderlyingException().toString());
                      e.printStackTrace();
              }
              catch (Exception e)
              {
                      e.printStackTrace();
              }
              return null;
      }
      //
      // Private class method readPublicKeyFromCol
      //
      private static PGPPublicKey readPublicKeyFromCol(InputStream in)
                     throws Exception {
              PGPPublicKeyRing pkRing = null;
              BcPGPPublicKeyRingCollection pkCol = new BcPGPPublicKeyRingCollection(PGPUtil.getDecoderStream(in));
              System.out.println("key ring size=" + pkCol.size());
              Iterator it = pkCol.getKeyRings();
              while (it.hasNext()) {
                      pkRing = (PGPPublicKeyRing) it.next();
                      Iterator pkIt = pkRing.getPublicKeys();
                      while (pkIt.hasNext()) {
                              PGPPublicKey key = (PGPPublicKey) pkIt.next();
                              System.out.println("Encryption key = " + key.isEncryptionKey() + ", Master key = " + 
                                                 key.isMasterKey());
                              if (key.isEncryptionKey())
                                      return key;
                      }
              }
              return null;
      }
      //
      // Private class method _encrypt
      //
      private static void _encrypt(String fileName, OutputStream out, PGPPublicKey encKey)
                          throws IOException, NoSuchProviderException, PGPException
      {
              out = new DataOutputStream(out);
              ByteArrayOutputStream bOut = new ByteArrayOutputStream();
              System.out.println("creating comData...");
              // get the data from the original file
              PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
              PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));
              comData.close();
              System.out.println("comData created...");
              System.out.println("using PGPEncryptedDataGenerator...");
              // object that encrypts the data
              BcPGPDataEncryptorBuilder builder = new BcPGPDataEncryptorBuilder(PGPEncryptedData.CAST5);
              builder.setSecureRandom(new SecureRandom());
              PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(builder);
              BcPublicKeyKeyEncryptionMethodGenerator encKeyGen = new BcPublicKeyKeyEncryptionMethodGenerator(encKey);
              cPk.addMethod(encKeyGen);
              System.out.println("used PGPEncryptedDataGenerator...");
              // take the outputstream of the original file and turn it into a byte
              // array
              byte[] bytes = bOut.toByteArray();
              System.out.println("wrote bOut to byte array...");
              // write the plain text bytes to the armored outputstream
              OutputStream cOut = cPk.open(out, bytes.length);
              cOut.write(bytes);
              cPk.close();
              out.close();
      }
      //
      // Private class method _decrypt
      //
      private static String _decrypt(InputStream in, InputStream keyIn,
                      char[] passwd) throws Exception {
              in = PGPUtil.getDecoderStream(in);
              try {
                      BcPGPObjectFactory pgpF = new BcPGPObjectFactory(PGPUtil.getDecoderStream(in));
                      PGPEncryptedDataList enc;
                      Object o = pgpF.nextObject();
                      //
                      // the first object might be a PGP marker packet.
                      //
                      if (o instanceof PGPEncryptedDataList) {
                              enc = (PGPEncryptedDataList) o;
                      } else {
                              enc = (PGPEncryptedDataList) pgpF.nextObject();
                      }
                      //
                      // find the secret key
                      //
                      Iterator it = enc.getEncryptedDataObjects();
                      PGPPrivateKey sKey = null;
                      PGPPublicKeyEncryptedData pbe = null;
                      while (sKey == null && it.hasNext()) {
                              pbe = (PGPPublicKeyEncryptedData) it.next();
                              System.out.println("pbe id=" + pbe.getKeyID());
                              sKey = findSecretKey(keyIn, pbe.getKeyID(), passwd);
                      }
                      if (sKey == null) {
                             throw new IllegalArgumentException("secret key for message not found.");
                      }
                      BcPublicKeyDataDecryptorFactory decryptorFactory=new BcPublicKeyDataDecryptorFactory(sKey); 
                      InputStream clear = pbe.getDataStream(decryptorFactory);
                      
                      BcPGPObjectFactory plainFact = new BcPGPObjectFactory(clear);
                      Object message = plainFact.nextObject();
                      if (message instanceof PGPCompressedData) {
                              PGPCompressedData cData = (PGPCompressedData) message;
                              BcPGPObjectFactory pgpFact = new BcPGPObjectFactory(PGPUtil.getDecoderStream(cData.getDataStream()));
                              message = pgpFact.nextObject();
                      }
                      ByteArrayOutputStream baos = new ByteArrayOutputStream();
                      if (message instanceof PGPLiteralData) {
                              PGPLiteralData ld = (PGPLiteralData) message;
                              InputStream unc = ld.getInputStream();
                              int ch;
                              while ((ch = unc.read()) >= 0) {
                                      baos.write(ch);
                              }
                      } else if (message instanceof PGPOnePassSignatureList) {
                              throw new PGPException("encrypted message contains a signed message - not literal data.");
                      } else {
                              throw new PGPException("message is not a simple encrypted file - type unknown.");
                      }
                      if (pbe.isIntegrityProtected()) {
                              if (!pbe.verify()) {
                                      System.err.println("message failed integrity check");
                              } else {
                                      System.err.println("message integrity check passed");
                              }
                      } else {
                              System.err.println("no message integrity check");
                      }
                      return baos.toString();
              } catch (PGPException e) {
                      System.err.println(e);
                      if (e.getUnderlyingException()!= null) {
                              e.getUnderlyingException().printStackTrace();
                      }
              }
              return null;
      }
      //
      // Private class method findSecretKey
      //
      private static PGPPrivateKey findSecretKey(InputStream keyIn, long keyID,
                      char[] pass) throws IOException, PGPException,
                      NoSuchProviderException {
              BcPGPSecretKeyRingCollection pgpSec = new BcPGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn));
              PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
              if (pgpSecKey == null) {
                      return null;
              }
              
              return pgpSecKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass));
      }
      //
      // Public class method readFile
      //
      public byte[] readFile(File file) throws IOException {
              FileInputStream fis = new FileInputStream(file);
              byte[] buf = new byte[4096];
              int numRead = 0;
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              while ((numRead = fis.read(buf)) > 0) {
                      baos.write(buf, 0, numRead);
              }
              fis.close();
              byte[] returnVal = baos.toByteArray();
              baos.close();
              return returnVal;
      }
      //
      // Public main method
      //
      public static void main(String[] args) {
              Security.addProvider(new BouncyCastleProvider());
              String TOKEN = "aamine";
              // ----- Encrypt the message to a file
              // them
              byte[] encdata = encrypt(TOKEN.getBytes());
              System.out.println("Encrypted: " + encdata);
              // Encode the byte array to a string
              //return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));	
              String temp = Base64.encodeBase64String(encdata);
              System.out.println("Temp: " + temp);
              // us
              byte[] newB=null;		
              try {
                      newB = Base64.decodeBase64(temp);
              } catch (Exception e) {
                      System.out.println("Exception: " + e);
              }
              System.out.println("byte array" + newB.length);
              // ----- Decrypt the token that
              String result = decrypt(newB);
              System.out.println("Decrypted: " + result);
      }
}