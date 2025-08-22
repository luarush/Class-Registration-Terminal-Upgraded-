/*SHA-512 is not the most secure algorithm
*This is only for educational purposes
*BCrypt or Argon2 are more secure alternatives
*/

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHash {
    public static String saltGenerator(){
        //SecureRandom is used to generate a cryptographically strong random number
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16]; //16 bytes salt 
        secureRandom.nextBytes(salt); //Fills the byte array with random bytes
        String saltString = Base64.getEncoder().encodeToString(salt); //Encodes the byte array to a Base64 string

        return saltString; //Returns the Base64 encoded salt
    }

    //Method to hash the password with the salt
    //Throws Exception if the hashing algorithm is not found
    public static String hashPassword(String password, String salt) throws Exception {

        byte[] saltToBytes = Base64.getDecoder().decode(salt); //Decodes the Base64 salt string to bytes

        MessageDigest md = MessageDigest.getInstance("SHA-512"); //Creates a MessageDigest instance for SHA-512

        //Updates the MessageDigest with the password bytes
        md.update(saltToBytes); 
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8)); //Hashes the password with the salt

        return Base64.getEncoder().encodeToString(hashedPassword); //Encodes the hashed password to a Base64 string
    }

    
}

//https://dzone.com/articles/secure-password-hashing-in-java