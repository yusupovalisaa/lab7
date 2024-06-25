package utilities;

import models.User;
import org.apache.logging.log4j.core.async.AsyncLogger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;

public class Crypto {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "1234567890123456";

    public static User getEncryptedForm(User user) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(user.getPassword().getBytes());
            return new User(user.getLogin(),  Base64.getEncoder().encodeToString(encryptedBytes));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return user;
    }



}
