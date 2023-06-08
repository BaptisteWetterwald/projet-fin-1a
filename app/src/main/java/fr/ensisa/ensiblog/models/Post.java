package fr.ensisa.ensiblog.models;

import java.util.Date;
import java.util.List;

public class Post {
    private Date creation;
    private Topic topic;
    private User author;
    private List<Content> content;
    private Date expiration;

    public Post(){
        // empty constructor required for firebase
    }

    public Post(Date creation, Topic topic, User author, List<Content> content, Date expiration) {
        this.creation = creation;
        this.topic = topic;
        this.author = author;
        this.content = content;
        this.expiration = expiration;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
