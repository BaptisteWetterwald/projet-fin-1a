package fr.ensisa.ensiblog.models;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing the basic user on the application
 **/
public class User implements Serializable {

    private Email email;
    private String biographie;
    private String uid;
    private String photoUrl;

    /**
     * User constructor
     **/
    public User(){
        // empty constructor required for firebase
    }

    /**
     * User constructor
     @param email : User
     **/
    public User(Email email){
        setEmail(email);
    }

    /**
     * User constructor
     @param email : User email
     @param uid : User uid
     **/
    public User(Email email, String uid){
        setEmail(email);
        setUid(uid);
    }

    /**
     * User constructor
     @param email : User email
     @param uid : User uid
     @param photoUrl : User photo
     **/
    public User(Email email, String uid, String biographie, String photoUrl){
        setEmail(email);
        setUid(uid);
        setBiographie(biographie);
        setPhotoUrl(photoUrl);
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

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof User))
            return false;
        if (obj == this)
            return true;
        return this.getEmail().equals(((User) obj).getEmail());
    }
}
