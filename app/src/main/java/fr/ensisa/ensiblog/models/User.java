package fr.ensisa.ensiblog.models;

import java.util.Date;

public class User {

    private Date registered;
    private Email email;
    private Password password;

    public User(){
        // empty constructor required for firebase
    }

}
