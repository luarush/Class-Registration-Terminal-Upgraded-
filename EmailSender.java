import java.security.SecureRandom;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailSender {
    //Current verification code and its expiration time
    //These are used to store the code sent to the user and check its validity
    private static String currrentCode = null;
    private static long codeExpirationTime = 0;
    
    public static String sendVerificationCode(String recipientEmail) {

        String verificationCode = codeGenerator();
        currrentCode = verificationCode;
        //Set expiration time to 5 minutes from now
        //This is used to check if the code is still valid when the user enters it
        codeExpirationTime = System.currentTimeMillis() + 5 * 60 * 1000; 

        final String senderEmail = "sender@email.com";
        final String appPassword = "password";

        //Mailtrap's SMTP server details
        String host = "smtp.gmail.com";

        //Set up mail server properties
        Properties props = new Properties(); 
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, appPassword);
                }
            });

        try {

            //Create a new Mimemessage object
            Message message = new MimeMessage(session);
            //Set From, To, Subject, and Content
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Registration Terminal Verification Code");

            //Bold code in HTML content
            String htmlContent = "<p>You are receiving this email because you requested a verification code for your account.</p>" +
                     "<p>Your verification code is:<br>" +
                     "<span style='font-size: 18px;'><b>" + verificationCode + "</b></span></p>" +
                     "<p>If you did not request this code, please ignore this email.</p>";

            message.setContent(htmlContent, "text/html");


            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }

        return verificationCode;
    }

    //Generate a random verification code alphanumeric of 6 characters
    private static String codeGenerator(){
        String code = "abcdefghigklmnopqrstuvwsyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom randCodeGenerator = new SecureRandom();
        int lengthOfCode = 6;

        StringBuilder verificationCode = new StringBuilder(); //Used to build the verification code

        //Generate a random code of lengthOfCode
        for(int i = 0; i < lengthOfCode; i++){
            char randomChar = code.charAt(randCodeGenerator.nextInt(code.length()));
            verificationCode.append(randomChar); 
        }
        return verificationCode.toString(); //Return the generated code as a string
    }

    //Validate the entered code against the current code and check if it's expired
    public static boolean checkCodeValidity(String codeEntered){
        boolean isValid = currrentCode.equals(codeEntered);
        //Check if code is null or expired
        if(codeEntered == null){
            System.out.println("Verification code is invalid.");
            return false;
        }

        //Check if code has expired
        //currentTimeMillis() returns the current time in milliseconds
        if(System.currentTimeMillis() > codeExpirationTime){
            System.out.println("Verification code has expired. Please request a new one.");
            return false;
        }

        //Check if the code entered matches the current code
        if(isValid){
            currrentCode = null; //Reset the code after successful verification
        } else{
            System.out.println("Verification code is incorrect.");
        }

        return isValid; //Return true if code is valid and not expired
    }
}
