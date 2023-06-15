package fr.ensisa.ensiblog.ui.posts;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import fr.ensisa.ensiblog.FullScreenVideoActivity;
import fr.ensisa.ensiblog.MainActivity;
import fr.ensisa.ensiblog.R;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ContentType;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.PostWithFunction;

public class PostWithFunctionAdapter extends RecyclerView.Adapter<PostWithFunctionAdapter.ViewHolder> {
    private final List<PostWithFunction> postList;
    private long lastClickVideo;

    public PostWithFunctionAdapter(List<PostWithFunction> postList) {
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
        PostWithFunction postWithFunction = postList.get(position);
        holder.bind(postWithFunction);
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

        public void bind(PostWithFunction postWithFunction){
            Post post = postWithFunction.getPost();
            String avatarUrl = post.getAuthor().getPhotoUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Picasso.get().load(avatarUrl).into(avatar);
            }
            Email email = post.getAuthor().getEmail();
            firstName.setText(email.firstName());
            lastName.setText(email.lastName());
            String functionText = postWithFunction.getFunction();
            function.setText(functionText == null ? "Membre" : functionText);
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
                        textView.setAutoLinkMask(Linkify.ALL);
                        textView.setMovementMethod(LinkMovementMethod.getInstance());
                        textView.setPadding(0, 0, 0, 20);
                        layoutContent.addView(textView);
                        break;
                    case IMAGE:
                        ImageView imageView = new ImageView(itemView.getContext());
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(params);
                        imageView.setBackgroundResource(R.drawable.round_outline);
                        imageView.setClipToOutline(true);
                        String photoUrl = content.getData();
                        Log.d("n6a", "ICI photoUrl : " + photoUrl + ", gif:" + photoUrl.contains(".gif"));
                        if (photoUrl.contains(".gif"))
                            Glide.with(imageView.getContext()).asGif().load(photoUrl).into(imageView);
                        else
                            Glide.with(imageView.getContext()).load(photoUrl).into(imageView);
                        // gravity center for imageView
                        LinearLayout linearLayout = new LinearLayout(itemView.getContext());
                        linearLayout.setGravity(Gravity.CENTER);
                        linearLayout.addView(imageView);
                        imageView.setPadding(0, 0, 0, 20);
                        layoutContent.addView(linearLayout);
                        break;
                    case VIDEO:
                        VideoView videoView = new VideoView(itemView.getContext());
                        videoView.setScaleY(1.0f);
                        videoView.setVideoURI(Uri.parse(content.getData()));

                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                        videoView.setLayoutParams(params2);

                        FrameLayout frameLayout = new FrameLayout(itemView.getContext());
                        frameLayout.setBackgroundResource(R.drawable.round_outline);
                        frameLayout.setClipToOutline(true);
                        frameLayout.addView(videoView);

                        // gravity center
                        LinearLayout linearLayout2 = new LinearLayout(itemView.getContext());
                        linearLayout2.setGravity(Gravity.CENTER);
                        linearLayout2.addView(frameLayout);
                        linearLayout2.setPadding(0, 0, 0, 20);

                        layoutContent.addView(linearLayout2);
                        videoView.setOnPreparedListener(mp -> {
                            videoView.start();
                            videoView.pause();
                        });

                        videoView.setOnClickListener(new View.OnClickListener() {
                            private long lastClickTime = 0;
                            private boolean isDoubleClick = false;

                            @Override
                            public void onClick(View v) {
                                long currentTime = System.currentTimeMillis();

                                if (currentTime - lastClickTime < 300) {
                                    // Double click detected
                                    isDoubleClick = true;
                                }

                                lastClickTime = currentTime;

                                if (isDoubleClick) {
                                    // Open full-screen video activity
                                    Intent intent = new Intent(itemView.getContext(), FullScreenVideoActivity.class);
                                    intent.putExtra("videoUrl", content.getData());
                                    itemView.getContext().startActivity(intent);
                                } else {
                                    // Play/pause the video
                                    if (videoView.isPlaying()) {
                                        videoView.pause();
                                    } else {
                                        videoView.start();
                                    }
                                }

                                // Reset the double click flag after a delay
                                v.postDelayed(() -> isDoubleClick = false, 300);
                            }
                        });
                }
            }

            // add a delete button if the user is the author of the post or has a role >= 3

            Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic", "user"}, new Object[]{post.getTopic(), MainActivity.getUserModel()}).addOnSuccessListener(topicUsers -> {
                if (topicUsers.isEmpty()) {
                    Toast.makeText(itemView.getContext(), "Erreur lors de la récupération de l'utilisateur", Toast.LENGTH_SHORT).show();
                    Log.d("n6a", "Error : no user found");
                    return;
                }

                if (topicUsers.size() > 1) {
                    Toast.makeText(itemView.getContext(), "Erreur : plusieurs utilisateurs trouvés", Toast.LENGTH_SHORT).show();
                    Log.d("n6a", "Error : multiple users found");
                    return;
                }

                TopicUser currentTopicUser = topicUsers.get(0);

                boolean isModerator = currentTopicUser.getRole().getRole() >= 3;
                boolean isAuthor = post.getAuthor().equals(MainActivity.getUserModel());

                if (isModerator || isAuthor) {
                    Log.d("n6a", "Creating delete button for post " + post);
                    Button deleteButton = new Button(itemView.getContext());
                    deleteButton.setText("Supprimer");
                    deleteButton.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Supprimer le post");
                        builder.setMessage("Êtes-vous sûr de vouloir supprimer ce post ?");
                        builder.setPositiveButton("Oui", (dialog, which) -> {
                            String[] fields = new String[]{"topic", "author", "creation"};
                            Object[] values = new Object[]{post.getTopic(), post.getAuthor(), post.getCreation()};
                            Database.getInstance().removeFrom(Table.POSTS.getName(), fields, values).addOnSuccessListener(aVoid -> {
                                Toast.makeText(itemView.getContext(), "Post supprimé", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(itemView.getContext(), "Erreur lors de la suppression du post", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        });
                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
                        builder.show();
                    });
                    layoutContent.addView(deleteButton);
                }
            }).addOnFailureListener(e -> {
                Log.e("n6a", "Error getting topic user", e);
            });
        }
    }
}
