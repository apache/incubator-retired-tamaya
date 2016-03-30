package org.apache.tamaya.ui;

import java.util.Date;
import java.util.Objects;

/**
 * Created by atsticks on 29.03.16.
 */
public class User {

    private String userID = "-";
    private String fulLName = "<unknown>";
    private Date logInDate = new Date();

    public User(String userID, String fullName){
        this.userID = Objects.requireNonNull(userID);
        this.fulLName = fullName;
        if(fullName==null){
            this.fulLName = userID;
        }
    }

    public String getUserID() {
        return userID;
    }

    public String getFullName() {
        return fulLName;
    }

    public String getLoginDate(){
        return logInDate.toString();
    }
}
