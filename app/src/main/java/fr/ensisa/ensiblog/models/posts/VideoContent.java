package fr.ensisa.ensiblog.models.posts;

public class VideoContent extends Content {
    private String videoUrl;

    public VideoContent() {
        // empty constructor required for firebase
    }

    public VideoContent(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}