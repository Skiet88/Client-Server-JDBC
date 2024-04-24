
package za.ac.cput.StudentEnrolment;

import java.io.Serializable;


public class Subject  implements Serializable {
    String subjectCode, subjectName, duration;

    public Subject() {
    }

    public Subject(String subjectCode, String subjectName, String duration) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.duration = duration;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDuration() {
        return duration;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return subjectCode + "\t" + subjectName + "\t" + duration ;
    }
    
    
}
