import java.sql.*;

class populateDatabase {

    //Database connection object
    private static Connection conn = null;

    //Method to connect to the SQLite database
    protected static Connection connect() {
        //Database file location
        String url = "jdbc:sqlite:data/course-finder.sqlite";
        conn = null;

        //Connect to SQLite database
        try {
            conn = DriverManager.getConnection ( url );
        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
        }
        return conn;
    }

    //Gets all course records from the database
    //and returns them as an array of ClassSection objects
    public static ClassSection[] selectAll() {
        ClassSection[] courses = new ClassSection[ getCourseCount () ];
        int i = 0;

        String sql = "SELECT * FROM courses";
        try (Statement stmt = conn.createStatement ();
             ResultSet resultSet = stmt.executeQuery ( sql )) {

            //Loop through each row in the result set
            while (resultSet.next ()) {
                //Extract each column value from the row
                int regNumb = resultSet.getInt ( "registrationNumber" );
                int secNumb = resultSet.getInt ( "sectionNumber" );
                String courseNumb = resultSet.getString ( "courseNumber" );
                int credits = resultSet.getInt ( "credits" );
                String classDay = resultSet.getString ( "day" );
                
                //Create a new ClassSection object with the extracted values
                ClassSection classSec = new ClassSection ( courseNumb, secNumb, regNumb, credits, classDay );

                //Call the set time method to set start time
                int startTime = resultSet.getInt ( "startTime" );
                classSec.setStartTime ( startTime );

                //Restore enrolled students from the database
                //If there are enrolled students, split them and add to the ClassSection object
                String enrolledStudents = resultSet.getString ( "enrolledStudents" );
                if ( enrolledStudents != null && !enrolledStudents.trim ().isEmpty () ){
                String[] studentsIds = enrolledStudents.split ( "," );
                  for(String studentId : studentsIds){
                      if ( !studentId.trim().isEmpty() ) {
                          classSec.addStudent ( studentId );
                      }
                  }

                }
                //add the ClassSection object to the array
                courses[ i++ ] = classSec;
            }

        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
        }

        return courses; //return an array of courses
    }


    //Insert initial course and student data into the database
    public static void insert() {
        try {
            Statement stmt = conn.createStatement ();

        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS courses (" +
            "registrationNumber INTEGER PRIMARY KEY, " +
            "sectionNumber INTEGER, " +
            "courseNumber TEXT, " +
            "day TEXT, " +
            "startTime INTEGER, " +
            "credits INTEGER, " +
            "enrolledStudents TEXT" +
            ")"
        );

            // Drop and recreate students table if it doesn't exist
            stmt.executeUpdate ( 
                "CREATE TABLE IF NOT EXISTS students (" +
                "studentId TEXT PRIMARY KEY, " +
                "email TEXT, " +
                "salt TEXT, " + 
                "final_password_hash TEXT" +
                ")" 
                );            

        //Close the statement
        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
        }
    }

    //Update a course by adding a student ID to the enrolledStudents column
    public static void addStudentToCourse( String studentId, int registrationNumber ) {
        String sql = "UPDATE courses SET enrolledStudents = enrolledStudents || ?  WHERE registrationNumber = ?";

        try (PreparedStatement pstmt = conn.prepareStatement ( sql )) {
            //Append student ID with a comma to the enrolledStudents column
            //This allows multiple student IDs to be stored in the same column
            pstmt.setString ( 1, studentId + "," );
            pstmt.setInt ( 2, registrationNumber );

            //Execute the update statement
            pstmt.executeUpdate ();

        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
        }
    }

    //Get the total number of courses in the database
    public static int getCourseCount() {
        String sql = "SELECT COUNT(*) AS totalRows FROM courses";

        try (PreparedStatement stmt = conn.prepareStatement ( sql )) { 
            ResultSet resultSet = stmt.executeQuery ();
            resultSet.next ();
            return resultSet.getInt ( "totalRows" );

        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
    }
        return 0;
    }

    //Check if a student ID exists in the database
    public static boolean verifyStudentId( String studentId ) {
        String sql = "SELECT COUNT(*) AS count FROM students WHERE studentId = ?";
        try (PreparedStatement stmt = conn.prepareStatement ( sql )) {
            stmt.setString ( 1, studentId );
            ResultSet resultSet = stmt.executeQuery ();
            resultSet.next ();
            return resultSet.getInt ( "count" ) > 0;

        } catch (SQLException e) {
            System.out.println ( e.getMessage () );
        }
        return false;
    }
    // Get user's E-mail by student ID
    public static String getUserEmail(String studentId){
        String sql = "SELECT email FROM students WHERE studentId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            // If result is found, return the email
            if(rs.next()){
                return rs.getString("email");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null; // Return null if no email is found
    }
    
    // Get the salt for a given student ID
    public static String getSalt(String studentId){
        String sql = "SELECT salt FROM students WHERE studentId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, studentId);
        
            ResultSet resultSet = pstmt.executeQuery();

            // If result is found, return the salt
            if(resultSet.next()){
                return resultSet.getString("salt");

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if no salt is found
    }

    // Get the correct password hash for a given user
    public static String getHash(String studentId){
        // SQL query to get the correct password hash
        String sql = "SELECT final_password_hash FROM students WHERE studentId = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, studentId);
            ResultSet resultSet = pstmt.executeQuery();

            // If result is found, return the password hash
            if(resultSet.next()){
                return resultSet.getString("final_password_hash");

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null; // Return null if no hash is found
    }


    // Set user's defined passowrd hash and salt
    public static void setPassword(String studentId, String salt, String hash){
        String sql = "UPDATE students SET salt = ?, final_password_hash = ? WHERE studentId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, salt);
            pstmt.setString(2, hash);
            pstmt.setString(3, studentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Store the generated salt and final password hash in the database
    public static void storePassword(String studentId, String salt, String finalHash) {
        String sql = "UPDATE students SET salt = ?, final_password_hash = ? WHERE studentId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, salt);
            pstmt.setString(2, finalHash);
            pstmt.setString(3, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            }
    }

    //Check if student has a valid password
    public static boolean hashAPassword(String studentId){
        String sql = "SELECT final_password_hash FROM students WHERE studentId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, studentId);
            ResultSet resultSet = pstmt.executeQuery();

            // If result is found, return true
            if(resultSet.next()){
                String hash = resultSet.getString("final_password_hash");
                return hash != null && !hash.isEmpty();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no hash is found

    }


    //Method to close the connection when finished
    protected static void closeConnection() {
        try {
            conn.close ();
        } catch (Exception e) {
            throw new RuntimeException ( e );
        }
    }

}

/*
 * https://stackoverflow.com/questions/49497556/how-to-update-column-with-multiple-values
 */


