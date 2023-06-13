package fr.ensisa.ensiblog.models;

import java.io.Serializable;

public class TopicUser implements Serializable {
    private Topic topic;
    private User user;
    private Role role;

    private String fonction;

    public TopicUser(){
        // empty constructor required for firebase
    }

    public TopicUser(Topic topic, User user, Role role){
        setTopic(topic);
        setUser(user);
        setRole(role);
    }

    public TopicUser(Topic topic, User user, Role role, String fonction){
        setTopic(topic);
        setUser(user);
        setRole(role);
        setFonction(fonction);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TopicUser))
            return false;
        if (obj == this)
            return true;
        return this.getTopic().equals(((TopicUser) obj).getTopic()) && this.getUser().equals(((TopicUser) obj).getUser()) && this.getRole().equals(((TopicUser) obj).getRole());
    }
}
