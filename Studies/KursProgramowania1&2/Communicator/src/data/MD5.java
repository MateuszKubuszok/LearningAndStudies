package data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	MessageDigest Hasher;
	
	public MD5 () {
		try {
			Hasher = MessageDigest.getInstance ("MD5");
		} catch (NoSuchAlgorithmException e) {}
	}
	
	public String hash (String string) {
		Hasher.update (string.getBytes ());
		byte[] byteData = Hasher.digest ();
		
		StringBuffer sb = new StringBuffer ();
		for (int i = 0; i < byteData.length; i++)
        	sb.append(Integer.toString ((byteData[i] & 0xff) + 0x100, 16).substring (1));
		
		return sb.toString ();
	}
}
