package fr.ensisa.ensiblog.models;

import java.io.Serializable;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Role))
            return false;
        if (obj == this)
            return true;
        return this.getRole() == ((Role) obj).getRole();
    }
}
