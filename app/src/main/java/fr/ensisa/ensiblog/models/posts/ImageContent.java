package fr.ensisa.ensiblog.models.posts;

public class ImageContent extends Content {
    private String imageUrl;


    public ImageContent() {
        // empty constructor required for firebase
    }

    public ImageContent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageContent(Content content) {
        this.imageUrl = ((ImageContent)content).getImageUrl();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}