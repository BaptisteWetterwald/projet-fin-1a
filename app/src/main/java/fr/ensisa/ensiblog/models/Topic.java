package fr.ensisa.ensiblog.models;

import java.io.Serializable;

/**
 * Class representing a topic on the application
 **/
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

    /**
     * Override the basic equals function to adapt it for our app
     @param obj : param of the basic equals function
     **/
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Topic))
            return false;
        if (obj == this)
            return true;
        return this.getName().equals(((Topic) obj).getName()) && this.getDefaultRole().equals(((Topic) obj).getDefaultRole());
    }

    /**
     * Override the basic toString function to adapt it for our app
     **/
    @Override
    public String toString() {
        return "Topic{" +
                "name='" + name + '\'' +
                ", defaultRole=" + defaultRole +
                '}';
    }
}
