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

    public static ContentType fromString(String text) {
        for (ContentType b : ContentType.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
