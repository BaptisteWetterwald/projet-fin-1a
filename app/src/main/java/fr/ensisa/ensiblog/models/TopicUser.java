package fr.ensisa.ensiblog.models;
public class TopicUser {
    private Topic topic;
    private User user;
    private Role role;

    public TopicUser(){
        // empty constructor required for firebase
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
}
