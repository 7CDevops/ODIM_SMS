package com.openclassroom.cour.odim.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public enum AESUtil {
;
	private static final String ENCRYPTION_KEY = "RwcmlVpg";
	private static final String ENCRYPTION_IV = "4e5Wa71fYoT7MFEX";
	
	public static String encrypt(String src) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
			return Base64.encodeBytes(cipher.doFinal(src.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String encrypt(String src,String Password) {
		try {
            
            String cle16 = calculerHash(Password,"MD5").substring(7, 23);                
            Cipher aesCipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(cle16.getBytes(StandardCharsets.UTF_8), "AES");
            
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] byteCipherText = aesCipher.doFinal(src.getBytes());
            return Base64.encodeBytes(byteCipherText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	public static String decrypt(String src) {
		String decrypted = "";
		try {
			
            
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
			decrypted = new String(cipher.doFinal(Base64.decode(src)), StandardCharsets.UTF_16);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return decrypted;
	}
	
	public static String decrypt(String src, String Password) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String decrypted = "";
			
			
            String cle16 = calculerHash(Password,"MD5").substring(7, 23);                
            Cipher aesCipher = Cipher.getInstance("AES");
            SecretKeySpec secretKey = new SecretKeySpec(cle16.getBytes(StandardCharsets.UTF_8), "AES");

            aesCipher.init(Cipher.DECRYPT_MODE,secretKey);
			decrypted = new String(aesCipher.doFinal(Base64.decode(src)), StandardCharsets.UTF_8);

		return decrypted;

	}	
	
	static AlgorithmParameterSpec makeIv() {
        return new IvParameterSpec(ENCRYPTION_IV.getBytes(StandardCharsets.UTF_8));
	}
	
	static Key makeKey() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] key = md.digest(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
			return new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

        return null;
	}
	
	static Key makeKey(String mdp) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] key = md.digest(mdp.getBytes(StandardCharsets.UTF_8));
			return new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

        return null;
	}
	
	 private static String calculerHash(String text,String hash) {
         String result = null;
         try {                                
             MessageDigest msgDigest = MessageDigest.getInstance(hash);
             msgDigest.reset();
             msgDigest.update(text.getBytes(StandardCharsets.UTF_8));
             byte[] digest = msgDigest.digest();
             
             result = byteToHex(digest);                                                                
         } catch (NoSuchAlgorithmException ex) {
         }
         return result;
 }
     
     /**
  * Convertit des octets en leur representation hexadecimale (base 16),
  * chacun se retrouvant finalement 'non signe' et sur 2 caracteres.
  * 
  * @see ://java.sun.com/developer/technicalArticles/Security/AES/AES_v1.html
  */
 public static String byteToHex(byte[] bits) {
     if (bits == null) {
         return null;
     }
     StringBuffer hex = new StringBuffer(bits.length * 2); // encod(1_bit) => 2 digits
     for (int i = 0; i < bits.length; i++) {
         if (((int) bits[i] & 0xff) < 0x10) { // 0 < .. < 9
             hex.append("0");
         }
         hex.append(Integer.toString((int) bits[i] & 0xff, 16)); // [(bit+256)%256]^16
     }
     return hex.toString();
 }	
}
