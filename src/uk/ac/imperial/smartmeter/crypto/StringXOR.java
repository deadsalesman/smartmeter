package uk.ac.imperial.smartmeter.crypto;




import org.apache.commons.codec.binary.Base64;

public class StringXOR {

	    public String encode(String s, String key) {
	        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
	    }

	    public String decode(String s, String key) {
	        return new String(xorWithKey(base64Decode(s), key.getBytes()));
	    }

	    private byte[] xorWithKey(byte[] a, byte[] key) {
	        byte[] out = new byte[a.length];
	        for (int i = 0; i < a.length; i++) {
	            out[i] = (byte) (a[i] ^ key[i%key.length]);
	        }
	        return out;
	    }

	    private byte[] base64Decode(String s) {
	        return Base64.decodeBase64(s);
	    }

	    private String base64Encode(byte[] bytes) {
	    	Base64 x = new Base64();
	    	byte[] y = x.encode(bytes);
	    	String ret = new String(y);
	        return ret;

	    }
	}
