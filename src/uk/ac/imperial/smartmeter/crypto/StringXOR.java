package uk.ac.imperial.smartmeter.crypto;




import org.apache.commons.codec.binary.Base64;

public class StringXOR {

	    public static String encode(String s, String key) {
	        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
	    }

	    public static String decode(String s, String key) {
	        return new String(xorWithKey(base64Decode(s), key.getBytes()));
	    }

	    private static byte[] xorWithKey(byte[] a, byte[] key) {
	        byte[] out = new byte[a.length];
	        for (int i = 0; i < a.length; i++) {
	            out[i] = (byte) (a[i] ^ key[i%key.length]);
	        }
	        return out;
	    }

	    public static byte[] base64Decode(String s) {
	        return Base64.decodeBase64(s);
	    }

	    public static String base64Encode(byte[] bytes) {
	        return Base64.encodeBase64String(bytes).toString();

	    }
	}
