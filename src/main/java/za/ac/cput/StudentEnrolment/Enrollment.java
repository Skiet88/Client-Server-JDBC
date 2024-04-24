
package za.ac.cput.StudentEnrolment;

import java.io.Serializable;

public class Enrollment implements Serializable{
    
    private String studentNumber;
    private String subjectCode;
    private String enrollmentYear;

    public Enrollment() {
    }

    public Enrollment(String studentNumber, String subjectCode, String enrollmentYear) {
      
        this.studentNumber = studentNumber;
        this.subjectCode = subjectCode;
        this.enrollmentYear = enrollmentYear;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getEnrollmentYear() {
        return enrollmentYear;
    }

    

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setEnrollmentYear(String enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    @Override
    public String toString() {
        return studentNumber + "\t\t" + subjectCode + "\t" + enrollmentYear;
    }

    
    
}
