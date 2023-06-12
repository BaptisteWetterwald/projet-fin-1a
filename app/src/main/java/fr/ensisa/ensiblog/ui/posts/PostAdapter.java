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
import java.util.Objects;

import fr.ensisa.ensiblog.R;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ContentType;
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

        private final ImageView avatar;
        private final TextView firstName;
        private final TextView lastName;
        private final TextView function;
        private final TextView topic;
        private final TextView day;
        private final TextView hour;
        private final LinearLayout layoutContent;
        private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()); //"dd/MM/yyyy HH:mm:ss"

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            avatar = itemView.findViewById(R.id.img_avatar);
            firstName = itemView.findViewById(R.id.txt_firstname);
            lastName = itemView.findViewById(R.id.txt_lastname);
            function = itemView.findViewById(R.id.txt_function);
            topic = itemView.findViewById(R.id.txt_topic);
            day = itemView.findViewById(R.id.txt_date_day);
            hour = itemView.findViewById(R.id.txt_date_hour);
            layoutContent = itemView.findViewById(R.id.layout_content);
        }

        public void bind(Post post){
            // TODO: add avatar and function
            Email email = post.getAuthor().getEmail();
            firstName.setText(email.firstName());
            lastName.setText(email.lastName());
            String functionText = "Function";
            function.setText(functionText);
            topic.setText(post.getTopic().getName());
            String creation = simpleDateFormat.format(post.getCreation());
            String dayString = creation.split(" ")[0];
            String hourString = creation.split(" ")[1];
            day.setText(dayString);
            hour.setText(hourString);

            layoutContent.removeAllViews();

            List<Content> contents = post.getContent();
            for (Content content : contents) {

                ContentType contentType = ContentType.fromString(content.getType());

                switch (Objects.requireNonNull(contentType)){
                    case TEXT:
                        TextView textView = new TextView(itemView.getContext());
                        textView.setText(content.getData());
                        textView.setTextColor(Color.parseColor("#606060"));
                        textView.setGravity(Gravity.CENTER);
                        layoutContent.addView(textView);
                        break;
                    case IMAGE:
                        ImageView imageView = new ImageView(itemView.getContext());
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        imageView.setLayoutParams(params);
                        imageView.setBackgroundResource(R.drawable.round_outline);
                        imageView.setClipToOutline(true);
                        Picasso.get().load(Uri.parse(content.getData())).into(imageView);
                        layoutContent.addView(imageView);
                        break;
                    case VIDEO:
                        VideoView videoView = new VideoView(itemView.getContext());
                        videoView.setVideoURI(Uri.parse(content.getData()));
                        videoView.start();
                        layoutContent.addView(videoView);
                }

                /*if (content instanceof TextContent) {
                    TextView textView = new TextView(itemView.getContext());
                    textView.setText(((TextContent) content).getText());
                    textView.setTextColor(Color.parseColor("#606060"));
                    textView.setGravity(Gravity.CENTER);
                    layoutContent.addView(textView);
                } else if (content instanceof ImageContent) {
                    ImageView imageView = new ImageView(itemView.getContext());
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(params);
                    imageView.setBackgroundResource(R.drawable.round_outline);
                    imageView.setClipToOutline(true);
                    Picasso.get().load(Uri.parse(((ImageContent) content).getImageUrl())).into(imageView);
                    layoutContent.addView(imageView);
                } else if (content instanceof VideoContent) {
                    VideoView videoView = new VideoView(itemView.getContext());
                    videoView.setVideoURI(Uri.parse(((VideoContent) content).getVideoUrl()));
                    videoView.start();
                    layoutContent.addView(videoView);
                }*/
            }
        }
    }
}
