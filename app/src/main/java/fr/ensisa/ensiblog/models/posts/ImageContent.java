package fr.ensisa.ensiblog.models.posts;

public class ImageContent extends Content {
    private String imageUrl;

    public ImageContent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}