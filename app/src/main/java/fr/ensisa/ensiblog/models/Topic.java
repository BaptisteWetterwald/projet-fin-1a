package fr.ensisa.ensiblog.models;
public class Topic {

    private String name;
    private Role defaultRole;

    public Topic(){
        // empty constructor required for firebase
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(Role defaultRole) {
        this.defaultRole = defaultRole;
    }
}
