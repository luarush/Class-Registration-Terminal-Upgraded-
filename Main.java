public class Main {
    public static void main(String[] args) {
        //database population and insertion
        populateDatabase.connect();
        populateDatabase.insert();

        //Handles user login and registration
        LoginManager.loginSystem();
    }
}
