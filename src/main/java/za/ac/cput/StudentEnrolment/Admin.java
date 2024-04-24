
package za.ac.cput.StudentEnrolment;

import java.io.Serializable;

/**
 *
 * @author ASUS
 */
public class Admin implements Serializable  {
    private String adminName, adminSurname, adminUsername, adminPassword;

    public Admin() {
    }
     public Admin(String adminUsername, String adminPassword) {
       
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }
 
    public Admin(String adminName, String adminSurname, String adminUsername, String adminPassword) {
        this.adminName = adminName;
        this.adminSurname = adminSurname;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminSurname() {
        return adminSurname;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setAdminSurname(String adminSurname) {
        this.adminSurname = adminSurname;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public String toString() {
        return "Admin{" + "adminName=" + adminName + ", adminSurname=" + adminSurname + ", adminUsername=" + adminUsername + ", adminPassword=" + adminPassword + '}';
    }
    
    
}
