package fr.ensisa.ensiblog.models;

import java.io.Serializable;

/**
 * Class used to assign an admin role to a user
 **/
public class Admin implements Serializable {

    private Email email;

    public Admin(){
        // empty constructor required for firebase
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}
