package fr.ensisa.ensiblog.models.posts;

public class TextContent extends Content {
    private String text;

    public TextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}