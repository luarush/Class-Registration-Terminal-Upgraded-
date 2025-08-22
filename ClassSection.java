public class ClassSection {
     //Variables that belongs to the class
    private String courseNumber;
    private int sectionNumber;
    private int registrationNumber;
    private String day;
    private int credits;
    private int startTime;
    private String[] enrolledStudents;
    private int enrolledCount;

    //Constructors to set up new course section
    public ClassSection( String courseNumber, int sectionNumber, int registrationNumber, int credits, String day ) {
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.day = day;
        this.registrationNumber = registrationNumber;
        this.credits = credits;
        this.startTime = 9;
        this.enrolledStudents = new String[ 20 ]; //Holds up to 20 students
        this.enrolledCount = 0;

    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public int getRegistrationNumber() {
        return registrationNumber;
    }

    public String getDay() {
        return day;
    }

    public int getCredits() {
        return credits;
    }

    public int getStartTime() {
        return startTime;
    }
    //Count the number of enrolled students
    public int getEnrolledNumber() {
        int count = 0;
        for(String enrolledNumb : enrolledStudents){
            if(enrolledNumb != null){
                count++;
            }
        }
        return count;
    }

    //Returns a list of enrolled students IDs in comma separated format
    public StringBuilder getEnrolledStudentList(){
        StringBuilder enrolledIds = new StringBuilder(); //Used to build the string of enrolled students

        for(String student : enrolledStudents){
            if(student != null){
                enrolledIds.append(student).append(",");
            }
        }
        return enrolledIds;
    }

    //Returns the amount of seats available in the class section
    public int getSeatsAvailable(){
        int maxTotallSeats = 20;
        return maxTotallSeats - getEnrolledNumber();
    }

    //24 hour format for start time
    //if time goes over 23, it will return to 0
    public void setStartTime(int startTime) {
        this.startTime = startTime;
        if (this.startTime > 23) {
            this.startTime -= 24;
        }
    }

    //Check if student is already enrolled in class section
    public boolean studentIsEnrolled(String isEnrolled){
        for(String enrolledStudent : enrolledStudents){
            if (enrolledStudent != null && enrolledStudent.equals(isEnrolled)) {
                return true;
            }
        }
        //If student is not enrolled, return false
        return false;
    }

    //Add student to the course section
    public boolean addStudent(String studentId){
        if (studentIsEnrolled(studentId)) {
            return false; //Student is already enrolled
        }

        if (enrolledCount < 20) {
           enrolledStudents[enrolledCount] = studentId; //Add student to the list
           enrolledCount++;
           return true;
        }

        //Class is full
        return false;
    }

    //Display course information in a formatted string
    public String toString(){
        return String.format("%9s %3d %12d %10d %8s %2d %10d", courseNumber, sectionNumber, registrationNumber, credits, day, startTime, getSeatsAvailable());
    }
}

//https://medium.com/javarevisited/5-effective-string-practices-you-should-know-e9a75811b123
 