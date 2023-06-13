package fr.ensisa.ensiblog.models.posts;

public class TextContent extends Content {
    private String text;

    public TextContent(String text) {
        this.text = text;
    }

    public TextContent() {
        // empty constructor required for firebase
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}