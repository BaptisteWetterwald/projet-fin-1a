package fr.ensisa.ensiblog.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;

public class Authentication {
    private FirebaseAuth mAuth;

    public Authentication(){
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser(){
        return mAuth.getCurrentUser();
    }

    public Task<AuthResult> createUser(String email, String password) throws RuntimeException {
        if(!(Email.isValid(email) && Password.isValid(password))){
            throw new RuntimeException();
        }
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> signInUser(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<Void> changePassword(String password){
        return getCurrentUser().updatePassword(password);
    }

    public void signOut(){
        mAuth.signOut();
    }

}
