package fr.ensisa.ensiblog.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class User {

    private Email email;
    private String fonction;
    private String biographie;
    private String uid;

    public User(){
        // empty constructor required for firebase
    }

    public User(Email email){
        setEmail(email);
    }

    public User(Email email, String uid){
        setEmail(email);
        setUid(uid);
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getBiographie() {
        return biographie;
    }

    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }
}
