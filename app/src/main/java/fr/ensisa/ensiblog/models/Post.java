package fr.ensisa.ensiblog.models;

import java.util.Date;
import java.util.List;

public class Post {
    private Date creation;
    private Topic topic;
    private User author;
    private List<Content> content;
    private Date expiration;
    
}
