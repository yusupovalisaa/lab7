package util;

import models.User;
import org.apache.logging.log4j.core.async.AsyncLogger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;

public class Decrypto {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    public static User getDencryptedForm(User user) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(user.getPassword());
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new User(user.getLogin(), new String(decryptedBytes));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return user;
    }
}
