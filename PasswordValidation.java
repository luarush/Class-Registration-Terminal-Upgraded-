import java.util.Scanner;

public class PasswordValidation {
   public static final Scanner kbInput = new Scanner(System.in);

    public static boolean passwordSetup(String userNum){
        String email = populateDatabase.getUserEmail(userNum);

        //If email is nto found, display a message to the user 
        if(email == null){
            System.out.println("\nError: No Email on file.");
            return false;
        }

        //Verification code generation and sending
        while(true){
            EmailSender.sendVerificationCode(email);
            System.out.print("\nVerification code has been sent to your Email: " + email);
            System.out.print("\nIf you don't receive the code, please, check your spam folder.\n");

            System.out.print("\nEnter the verification code: ");
            String codeEntered = kbInput.nextLine().trim(); //Read user input for the verification code and trim whitespace

            //Check if codeEntered is valid
            if(EmailSender.checkCodeValidity(codeEntered)){
                break; //Exit the loop if the code is valid
            } else{
                //If the code is not valid, ask the user to try again
                System.out.print("\nVerification code is invalid or expired.");
                System.out.print("Try again? (Y/N): ");
                String tryAgain = kbInput.nextLine().trim().toLowerCase();

                //If user doesn't want to try again, exit the loop
                if(!tryAgain.equals("Y")){
                    return false; //Exit the method if user chooses not to try again
                }

            }
        }


        //Ask user to create a password
        //Loop until a valid password is created    
        while(true){
            System.out.print("\nCreate a password: ");
            String newPassword = kbInput.nextLine();                
            
            //Check if the password meets the requirements
            if(newPassword.length() < 8 || newPassword.contains(" ")){
                System.out.println("\nPassword must be at least 8 characters long. Please try again.");
                continue;
            } 
            if(newPassword.contains(" ")){
                System.out.println("\nPassword cannot contain spaces. Please try again.");
                continue;
            } 
            if (newPassword.equals("password") || newPassword.equals("12345678") || newPassword.equals("qwertyui")){
                System.out.println("\nPassword is too common. Please choose a more secure password.");
                continue;
            } 
            if(newPassword.equals(userNum)){
                System.out.println("\nPassword cannot be the same as your user number. Please try again.");
                continue;
            } 
            if(!newPassword.matches(".*[A-Z].*") 
            || !newPassword.matches(".*[a-z].*")
            || !newPassword.matches(".*[0-9].*") 
            || !newPassword.matches(".*[!@#$%&*()_+=|<>?{}\\[\\]~-].*")){
                System.out.println("\nPassword must contain at least one letter, one number, and one special character. Please try again.");
                continue;
            }

            //If the password meets the requirements, ask user to confirm it
            System.out.print("\nConfirm your password: ");
            String confirmPassword = kbInput.nextLine();
            
            //Check if the passwords match
            if(!confirmPassword.equals(newPassword)){
                System.out.println("\nPasswords do not match. Please try again.");
                continue;
            }
            

            //Create salt and hash the password
            String salt = PasswordHash.saltGenerator();
            String newHash = "";

            //Hash the password with the generated salt
            //Handle any exceptions that may occur during hashing
            try {
                newHash = PasswordHash.hashPassword(newPassword, salt);
            } catch (Exception e) {
                System.out.println("\nError creating password hash: " + e.getMessage());
                return false; //Return false if there was an error during hashing
            }

            //Populate the database with the new password and salt
            populateDatabase.storePassword(userNum, salt, newHash);
            return true; //Successful password setup
        }
    }

    //Check if the password is valid for the user
    public void checkCodeValidity(String userNum){
        boolean isValid = false;

        //Loop until the password is valid or user chooses to reset it
        while (!isValid) {
            System.out.print("\nEnter your passwordor type '1' if you forgot it: ");
            String password = kbInput.nextLine();
            
            //If user wants to reset the password
            if(password.equals("1")){
                boolean passowrdReset = PasswordReset(userNum);

                if(passowrdReset){
                    System.out.println("\nYour password has been reset successfully.");
                } else{
                    System.out.println("\nPassword reset failed. Returning to password prompt.");
                }
                continue; //Continue to the next iteration of the loop
            }
            
            //Verify entered password
            String storedSalt = populateDatabase.getSalt(userNum);
            String storedHash = populateDatabase.getHash(userNum);

            try {
                String enteredHash = PasswordHash.hashPassword(password, storedSalt);

                //Check if the entered hash matches the stored hash
                if(enteredHash.equals(storedHash)){
                    System.out.println("\nLogin successful.");
                    isValid = true; //Set isValid to true to exit the loop
                } else{
                    System.out.println("\nInvalid password. Please try again.");
                }

            } catch (Exception e) {
                System.out.println("An error occurred during password verification: " + e.getMessage());
            }

        }
        LoginManager.courseRegistration(userNum);
    }

    //Reset the password for the user
    public static boolean PasswordReset(String userNum){
        String email = populateDatabase.getUserEmail(userNum);
        if (email == null) {
            System.out.println("\nError: No Email on file.");
            return false; //Exit if no email is found
        }
        return passwordSetup(userNum); //Call password setup to reset the password
    }
}
