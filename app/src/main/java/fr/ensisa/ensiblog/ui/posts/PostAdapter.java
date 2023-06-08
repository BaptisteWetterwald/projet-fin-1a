package fr.ensisa.ensiblog.ui.posts;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import fr.ensisa.ensiblog.R;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ImageContent;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;
import fr.ensisa.ensiblog.models.posts.VideoContent;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup layoutContent;
        private ImageView imageViewContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutContent = itemView.findViewById(R.id.layout_content);
            imageViewContent = itemView.findViewById(R.id.image_content);
        }

        public void bind(Post post) {
            layoutContent.removeAllViews(); // Clear existing views

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            for (Content content : post.getContent()) {
                if (content instanceof TextContent) {
                    View contentView = inflater.inflate(R.layout.content_text, layoutContent, false);
                    TextView textViewContent = contentView.findViewById(R.id.text_content);
                    textViewContent.setText(((TextContent) content).getText());
                    layoutContent.addView(contentView);
                    contentView.setVisibility(View.VISIBLE);
                } else if (content instanceof ImageContent) {
                    View contentView = inflater.inflate(R.layout.content_image, layoutContent, false);
                    imageViewContent = contentView.findViewById(R.id.image_content);
                    Picasso.get().load(((ImageContent) content).getImageUrl()).into(imageViewContent);
                    layoutContent.addView(contentView);
                    contentView.setVisibility(View.VISIBLE);
                } else if (content instanceof VideoContent) {
                    View contentView = inflater.inflate(R.layout.content_video, layoutContent, false);
                    VideoView videoViewContent = contentView.findViewById(R.id.video_content);
                    videoViewContent.setVideoURI(Uri.parse(((VideoContent) content).getVideoUrl()));
                    layoutContent.addView(contentView);
                    contentView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
