package fr.ensisa.ensiblog.models;
public class Role {
    private short role;

    public Role(){
        // empty constructor required for firebase
    }

    public short getRole() {
        return role;
    }

    public void setRole(short role) {
        this.role = role;
    }
}
