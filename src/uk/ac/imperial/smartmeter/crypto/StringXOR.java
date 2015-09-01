package uk.ac.imperial.smartmeter.crypto;




import org.apache.commons.codec.binary.Base64;

/**
 * Utility class to allow for elementary string manipulation in later versions of java.
 * @author bwindo
 *
 */
public class StringXOR {

	/**
	 * Logical XORs a string with a given key. Note: f(f(x))!=f^-1(f(x)) as
	 * encode is not involutary.
	 * 
	 * @param s
	 *            The string to be XORed with the key.
	 * @param key
	 *            The key to be XORed with the string.
	 * @return The XORed string.
	 */
	public static String encode(String s, String key) {
		return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
	}

	/**
	 * Performs the inverse of the logical XOR operation acting on a string and a key.
	 * @param s The string to be inverse XORed with the key.
	 * @param key The key to be inverse XORed with the string.
	 * @return The decoded string.
	 */
	public static String decode(String s, String key) {
		return new String(xorWithKey(base64Decode(s), key.getBytes()));
	}

	/**
	 * XORs two byte arrays.
	 * 
	 * @param a
	 *            The byte array to be XORed with the key.
	 * @param key
	 *            The key to be XORed with the byte array.
	 * @return The XORed arrays.
	 */
	    private static byte[] xorWithKey(byte[] a, byte[] key) {
	        byte[] out = new byte[a.length];
	        for (int i = 0; i < a.length; i++) {
	            out[i] = (byte) (a[i] ^ key[i%key.length]);
	        }
	        return out;
	    }

	    /**
	     * Returns the decoding of a base 64 string.
	     * @param s The given string.
	     * @return The byte array encoding the representation of the given base 64 string.
	     */
	    public static byte[] base64Decode(String s) {
	        return Base64.decodeBase64(s);
	    }

	/**
	 * Returns the base64 encoding of a string.
	 * @param bytes The given string to be encoded.
	 * @return The string encoding the original string in base 64.
	 */
	    public static String base64Encode(byte[] bytes) {
	        return Base64.encodeBase64String(bytes).toString();

	    }
	}
