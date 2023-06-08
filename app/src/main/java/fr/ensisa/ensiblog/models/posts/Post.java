package fr.ensisa.ensiblog.models.posts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.User;

public class Post {
    private Date creation;
    private Topic topic;
    private User author;
    private List<Content> content = new ArrayList<>();
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

    public void addContent(Content content) {
        this.content.add(content);
    }
}
