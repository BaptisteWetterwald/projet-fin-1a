package fr.ensisa.ensiblog.models.posts;

public class VideoContent extends Content {
    private String videoUrl;

    public VideoContent(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}