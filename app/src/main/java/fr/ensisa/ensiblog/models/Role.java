package fr.ensisa.ensiblog.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Class used to assign a role to a user
 **/
public class Role implements Serializable {

    // Super-Modo : 4
    // Modo : 3
    // Editor : 2
    // Reader : 1
    private int role;

    public Role(){
        // empty constructor required for firebase
    }

    public Role(int role){
        this.role = role;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    /**
     * Override the basic equals function to adapt it for our app
     @param obj : param of the basic equals function
     **/
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Role))
            return false;
        if (obj == this)
            return true;
        return this.getRole() == ((Role) obj).getRole();
    }

    @NonNull
    @Override
    public String toString() {
        return "Role{" +
                "role='" + role + '\'' +
                '}';
    }
}
