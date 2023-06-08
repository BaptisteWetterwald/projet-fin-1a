package fr.ensisa.ensiblog.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import fr.ensisa.ensiblog.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on the content type
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case ContentType.TEXT:
                view = inflater.inflate(R.layout.item_text_content, parent, false);
                return new TextContentViewHolder(view);
            case ContentType.IMAGE:
                view = inflater.inflate(R.layout.item_image_content, parent, false);
                return new ImageContentViewHolder(view);
            case ContentType.VIDEO:
                view = inflater.inflate(R.layout.item_video_content, parent, false);
                return new VideoContentViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid content type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        List<Content> contents = post.getContent();

        for (Content content : contents) {
            if (content instanceof Text) {
                TextContentViewHolder textHolder = (TextContentViewHolder) holder;
                Text textContent = (Text) content;
                // Bind text content to the views in the TextContentViewHolder
                textHolder.textView.setText(textContent.getText());
            } else if (content instanceof Image) {
                ImageContentViewHolder imageHolder = (ImageContentViewHolder) holder;
                Image imageContent = (Image) content;
                // Bind image content to the views in the ImageContentViewHolder
                // Load the image using an image loading library like Glide or Picasso

                Bitmap bmp = BitmapFactory.decodeByteArray(imageContent.getData(), 0, imageContent.getData().length);

                //imageHolder.imageView.setImageUrl(imageContent.getImageUrl());

                imageHolder.imageView.setImageBitmap(bmp);
                Log.i("n6a", "imageContent.getData(): " + Arrays.toString(imageContent.getData()));

            } else if (content instanceof Video) {
                VideoContentViewHolder videoHolder = (VideoContentViewHolder) holder;
                Video videoContent = (Video) content;
                // Bind video content to the views in the VideoContentViewHolder
                // Load the video using a video player library
                //videoHolder.videoPlayer.setVideoUrl(videoContent.getVideoUrl());
                videoHolder.videoView.setVideoPath(videoContent.getPath());
                videoHolder.videoView.start();
            }
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        List<Content> contents = posts.get(position).getContent();
        if (contents.isEmpty()) {
            throw new IllegalStateException("Post has no content");
        }

        Content firstContent = contents.get(0);
        if (firstContent instanceof Text) {
            return ContentType.TEXT;
        } else if (firstContent instanceof Image) {
            return ContentType.IMAGE;
        } else if (firstContent instanceof Video) {
            return ContentType.VIDEO;
        } else {
            throw new IllegalArgumentException("Invalid content type: " + firstContent.getClass().getSimpleName());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class TextContentViewHolder extends ViewHolder {
        TextView textView;

        public TextContentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views in the TextContentViewHolder
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public static class ImageContentViewHolder extends ViewHolder {
        ImageView imageView;

        public ImageContentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views in the ImageContentViewHolder
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    public static class VideoContentViewHolder extends ViewHolder {
        VideoView videoView;

        public VideoContentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views in the VideoContentViewHolder
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}

// Define a class to hold constants for content types
class ContentType {
    static final int TEXT = 1;
    static final int IMAGE = 2;
    static final int VIDEO = 3;
}
