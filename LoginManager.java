import java.util.Scanner;

import javax.swing.JOptionPane;

public class LoginManager {
    static final Scanner kbInput = new Scanner(System.in);

    public static void loginSystem() {
        System.out.println("\nWelcome to the Registration Enrollment System");
        System.out.println("Please, log in to continue.");

        PasswordValidation passVal = new PasswordValidation();
        
        //Loop for user login
        while(true){
            System.out.print("Enter your 6-digit user number (J-number): ");
            String userNum = kbInput.nextLine().toUpperCase().trim();

            //Check if the user number is valid
            if(userNum.equals("0")){
                break; //Exit the login loop if user enters 0
            }

            if (!populateDatabase.verifyStudentId(userNum)) {
                System.out.println("\nInvalide user number. Please, enter a valid 6-digit user number. ");
                continue; //Continue to the next iteration of the loop
            }

            //Check if user needs to create a password
            if(!populateDatabase.hashAPassword(userNum)){
                System.out.println("\nFirst time logging. Please, set up your password.");

                boolean passowrdSetUp = PasswordValidation.passwordSetup(userNum);

                //Check if password setup was successful
                if (passowrdSetUp) {
                    System.out.println("\nPassword setup successful. You can now log in.");
                } else{
                    System.out.println("\nPassword setup failed.\n");
                }
                continue; //Continue to the next iteration of the loop

            }
            passVal.checkCodeValidity(userNum); //Check if the password is valid
            
        }
        
        System.out.println("\nRegistration termninal closed!");
        kbInput.close();
    }   

     //Runs the course registration terminal for a logged-in user.
    public static void courseRegistration(String userNum) {
        while (true) {
            System.out.println("\n--- Course Registration ---");
            System.out.println("Course     - Section - Registration #  - Credits    - Day/Time   - Available Seats");
            System.out.println("----------------------------------------------------------------------------------");

            ClassSection[] courses = populateDatabase.selectAll();
            for (ClassSection course : courses) {
                System.out.println(course);
            }

            System.out.print("\nEnter a registration number to enroll, or enter 0 to log out: ");
            int regNumber;
            try {
                regNumber = Integer.parseInt(kbInput.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number.");
                continue;
            }

            if (regNumber == 0) {
                System.out.println("\nLogging out for J-number " + userNum);
                System.out.println("-----------------------------------------------\n");
                break; // Exit registration loop
            }

            ClassSection selectedClass = null;
            for (ClassSection cs : courses) {
                if (cs.getRegistrationNumber() == regNumber) {
                    selectedClass = cs;
                    break;
                }
            }

            if (selectedClass == null) {
                System.out.println("\nInvalid registration number. Please try again.");
                continue;
            }

            if (selectedClass.studentIsEnrolled(userNum)) {
                JOptionPane.showMessageDialog(null,
                        "You are already enrolled in this class!",
                        "Enrollment Error",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            int currentCredits = 0;
            for (ClassSection c : courses) {
                if (c.studentIsEnrolled(userNum)) {
                    currentCredits += c.getCredits();
                }
            }

            if (currentCredits + selectedClass.getCredits() > 18) {
                JOptionPane.showMessageDialog(null,
                        "You cannot enroll in this course. You are already enrolled in " + currentCredits +
                        " credits and the maximum is 18 credits per semester.\n" +
                        "Please contact an advisor if you want to add more classes.",
                        "Credit Limit Reached",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            if (selectedClass.addStudent(userNum)) {
                populateDatabase.addStudentToCourse(userNum, regNumber);
                JOptionPane.showMessageDialog(null,
                        "Successfully enrolled in " + selectedClass.getCourseNumber() + "!",
                        "Enrollment Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Unable to enroll. The course is full.",
                        "Enrollment Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
