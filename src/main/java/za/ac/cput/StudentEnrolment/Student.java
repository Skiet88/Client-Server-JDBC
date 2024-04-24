
package za.ac.cput.StudentEnrolment;

import java.io.Serializable;


public class Student  implements Serializable {
    private String stdName, stdSurname, stddNum, stdPassword;

    public Student() {
    }

    public Student(String stdName, String stdSurname, String stddNum, String stdPassword) {
        this.stdName = stdName;
        this.stdSurname = stdSurname;
        this.stddNum = stddNum;
        this.stdPassword = stdPassword;
    }

    public Student(String stddNum, String stdPassword) {
        this.stddNum = stddNum;
        this.stdPassword = stdPassword;
    }
    

    public String getStdName() {
        return stdName;
    }

    public String getStdSurname() {
        return stdSurname;
    }

    public String getStddNum() {
        return stddNum;
    }

    public String getStdPassword() {
        return stdPassword;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public void setStdSurname(String stdSurname) {
        this.stdSurname = stdSurname;
    }

    public void setStddNum(String stddNum) {
        this.stddNum = stddNum;
    }

    public void setStdPassword(String stdPassword) {
        this.stdPassword = stdPassword;
    }

   @Override
    public String toString() {
        return stdName + "\t\t" + stdSurname + "\t" + stddNum + "\t" + stdPassword;
    }
    
    
}
