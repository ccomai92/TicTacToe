package AES;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Shoreline Community College
 * @author Sahand Milaninia, Haram Kwon, Cory Bakich
 */

public class AES {

    //Create Byte array of the password
    private static byte[] encrypt(String message) throws Exception {
        String salt = "1111111111111111";
        SecretKeySpec key = new SecretKeySpec(salt.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(message.getBytes());
    }

    /**
     * Convert a password to a encrypted password
     * if fails, return null.
     *
     * @param password
     * @return
     */
    public static String convertToString(String password) {
        String AESPassword;
        try {
            AESPassword = new String(encrypt(password));
            return AESPassword;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}