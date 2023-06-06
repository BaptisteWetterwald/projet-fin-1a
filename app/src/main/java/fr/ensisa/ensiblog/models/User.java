package fr.ensisa.ensiblog.models;

import java.util.Date;

public class User {

    private Date registered;
    private Email email;
    private Password password;

    public User(){
        // empty constructor required for firebase
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }
}
