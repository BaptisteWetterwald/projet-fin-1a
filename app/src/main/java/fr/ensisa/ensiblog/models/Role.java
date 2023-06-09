package fr.ensisa.ensiblog.models;
public class Role {

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
}
