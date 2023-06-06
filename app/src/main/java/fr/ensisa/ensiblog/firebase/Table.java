package fr.ensisa.ensiblog.firebase;

public enum Table {

    // for each model, add the corresponding table name

    TOPICS("Topics"),
    USERS("Users"),
    POSTS("Posts"),
    ADMINS("Admins"),
    TOPIC_USERS("TopicUsers");

    private final String name;

    Table(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
