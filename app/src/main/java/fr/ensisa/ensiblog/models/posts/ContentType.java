package fr.ensisa.ensiblog.models.posts;

public enum ContentType {

    TEXT("TextContent"),
    VIDEO("VideoContent"),
    IMAGE("ImageContent");

    private final String type;

    ContentType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

}
