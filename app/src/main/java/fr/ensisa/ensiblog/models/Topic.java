package fr.ensisa.ensiblog.models;

import java.io.Serializable;

public class Topic implements Serializable {

    private String name;
    private Role defaultRole;

    public Topic(){
        // empty constructor required for firebase
    }

    public Topic(Topic topic){
        this.name = topic.getName();
        this.defaultRole = topic.getDefaultRole();
    }

    public Topic(String name, Role defaultRole){
        this.name = name;
        this.defaultRole = defaultRole;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Topic))
            return false;
        if (obj == this)
            return true;
        return this.getName().equals(((Topic) obj).getName()) && this.getDefaultRole().equals(((Topic) obj).getDefaultRole());
    }
}
