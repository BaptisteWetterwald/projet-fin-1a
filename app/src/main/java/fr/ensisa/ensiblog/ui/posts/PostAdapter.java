package fr.ensisa.ensiblog.ui.posts;

import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.ensisa.ensiblog.R;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ImageContent;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;
import fr.ensisa.ensiblog.models.posts.VideoContent;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private final List<Post> postList;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layoutContent;
        private final LinearLayout layoutHeader;
        private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()); //"dd/MM/yyyy HH:mm:ss"

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layout_header);
            layoutContent = itemView.findViewById(R.id.layout_content);

            LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutHeader.setLayoutParams(headerParams);
            layoutHeader.setBackgroundColor(Color.parseColor("#99bbff"));

            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutContent.setLayoutParams(contentParams);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(60, 60, 60, 60);
            itemView.setLayoutParams(params);
            itemView.setBackgroundResource(R.drawable.round_outline);
            itemView.setClipToOutline(true);
        }

        public void bind(Post post) {

            // Create a LayoutInflater to inflate views from the XML layout
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            LinearLayout leftPartHeader = new LinearLayout(itemView.getContext());
            leftPartHeader.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams paramsLeftPartHeader = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            leftPartHeader.setLayoutParams(paramsLeftPartHeader);
            layoutHeader.addView(leftPartHeader);

            LinearLayout rightPartHeader = new LinearLayout(itemView.getContext());
            rightPartHeader.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams paramsRightPartHeader = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            paramsRightPartHeader.gravity = android.view.Gravity.END;
            rightPartHeader.setLayoutParams(paramsRightPartHeader);
            rightPartHeader.setPadding(20, 0, 20, 0);
            layoutHeader.addView(rightPartHeader);

            // Horizontal left : avatar, author and date
            ImageView avatar = new ImageView(itemView.getContext());
            avatar.setImageResource(R.drawable.ic_user);
            avatar.setAdjustViewBounds(true);
            avatar.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams paramsAvatar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            paramsAvatar.gravity = android.view.Gravity.CENTER_VERTICAL;
            avatar.setLayoutParams(paramsAvatar);
            avatar.setMaxWidth(200);
            avatar.setMaxHeight(200);
            avatar.setMinimumWidth(100);
            avatar.setMinimumHeight(100);

            leftPartHeader.addView(avatar);

            LinearLayout namesVertical = new LinearLayout(itemView.getContext());
            namesVertical.setOrientation(LinearLayout.VERTICAL);
            namesVertical.setPadding(20, 0, 0, 0);

            Email authorEmail = post.getAuthor().getEmail();

            TextView firstnameTextView = new TextView(itemView.getContext());
            firstnameTextView.setText(authorEmail.firstName());
            firstnameTextView.setTextColor(Color.parseColor("#003cb3"));
            namesVertical.addView(firstnameTextView);

            TextView lastnameTextView = new TextView(itemView.getContext());
            lastnameTextView.setText(authorEmail.lastName());
            lastnameTextView.setTextColor(Color.parseColor("#003cb3"));
            namesVertical.addView(lastnameTextView);

            leftPartHeader.addView(namesVertical);

            // Horizontal right : date, topic

            Date creation = post.getCreation();
            String creationString = simpleDateFormat.format(creation);
            String day = creationString.split(" ")[0];
            String hour = creationString.split(" ")[1];

            LinearLayout layoutDate = new LinearLayout(itemView.getContext());
            layoutDate.setOrientation(LinearLayout.VERTICAL);

            TextView dayView = new TextView(itemView.getContext());
            dayView.setText(day);
            layoutDate.addView(dayView);

            TextView hourView = new TextView(itemView.getContext());
            hourView.setText(hour);
            layoutDate.addView(hourView);

            rightPartHeader.addView(layoutDate);

            TextView topicTextView = new TextView(itemView.getContext());
            topicTextView.setText(post.getTopic().getName());
            topicTextView.setPadding(50, 0, 0, 0);
            rightPartHeader.addView(topicTextView);

            for (Content content : post.getContent()) {
                View contentView;

                if (content instanceof TextContent) {
                    contentView = inflater.inflate(R.layout.content_text, layoutContent, false);
                    TextView textViewContent = contentView.findViewById(R.id.text_content);
                    textViewContent.setText(((TextContent) content).getText());
                    textViewContent.setTextColor(Color.parseColor("#606060"));
                    textViewContent.setPadding(15, 0, 15, 0);
                } else if (content instanceof ImageContent) {
                    contentView = inflater.inflate(R.layout.content_image, layoutContent, false);
                    ImageView imageViewContent = contentView.findViewById(R.id.image_content);
                    Picasso.get().load(((ImageContent) content).getImageUrl()).into(imageViewContent);
                } else if (content instanceof VideoContent) {
                    contentView = inflater.inflate(R.layout.content_video, layoutContent, false);
                    VideoView videoViewContent = contentView.findViewById(R.id.video_content);
                    videoViewContent.setVideoURI(Uri.parse(((VideoContent) content).getVideoUrl()));
                    videoViewContent.start();
                } else {
                    throw new RuntimeException("Unknown content type");
                }

                // Add the content view to the layout
                layoutContent.addView(contentView);
            }

        }
    }
}
