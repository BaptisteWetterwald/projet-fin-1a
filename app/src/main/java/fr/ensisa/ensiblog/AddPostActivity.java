package fr.ensisa.ensiblog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ContentType;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;

public class AddPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        LinearLayout list_content = findViewById(R.id.list_content);

        ActivityResultLauncher<PickVisualMediaRequest> pickImage =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        ImageView imgView = new ImageView(AddPostActivity.this);
                        imgView.setContentDescription(uri.toString());
                        Picasso.get().load(uri).into(imgView);
                        list_content.addView(imgView);
                    }
                });
        ActivityResultLauncher<PickVisualMediaRequest> pickVideo =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        VideoView videoView = new VideoView(AddPostActivity.this);
                        videoView.setVideoURI(uri);
                        videoView.setContentDescription(uri.toString());
                        MediaController mediaController = new MediaController(AddPostActivity.this);
                        mediaController.setAnchorView(videoView);
                        mediaController.setMediaPlayer(videoView);
                        videoView.setMediaController(mediaController);
                        videoView.setScaleY(1.0f);
                        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500);
                        videoView.setLayoutParams(params2);
                        FrameLayout frameLayout = new FrameLayout(list_content.getContext());
                        frameLayout.setBackgroundResource(R.drawable.round_outline);
                        frameLayout.setClipToOutline(true);
                        frameLayout.addView(videoView);
                        list_content.addView(frameLayout);
                        videoView.start();
                    }
                });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            TextView userName = findViewById(R.id.textViewUsername);
            Email usrEmail = new Email(user.getEmail());
            userName.setText(usrEmail.firstName() + " " + usrEmail.lastName());

            TopicUser topicUser = (TopicUser) extras.get("topicUser");

            Button addImage = findViewById(R.id.buttonAddImage);
            Button addText = findViewById(R.id.buttonAddText);
            Button addVideo = findViewById(R.id.buttonAddVideo);

            addImage.setOnClickListener(v -> {
                pickImage.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            });

            addText.setOnClickListener(v -> {
                Log.i("n6a", "TEXT");
                EditText editText = new EditText(AddPostActivity.this);
                list_content.addView(editText);
            });

            addVideo.setOnClickListener(v -> {
                Log.i("n6a", "addVideo");

                Log.i("n6a", "pickMedia");
                pickVideo.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                        .build());
            });

            Button buttonPublish = findViewById(R.id.buttonPublish);

            buttonPublish.setOnClickListener(v -> {

                List<Content> listContent = new ArrayList<Content>();

                for (int i = 0; i < list_content.getChildCount(); i++) {
                    View element = list_content.getChildAt(i);
                    if(element instanceof TextView)
                        listContent.add(new Content(ContentType.TEXT,((TextView)element).getText().toString()));
                    else if (element instanceof ImageView)
                        listContent.add(new Content(ContentType.IMAGE, (String) ((ImageView)element).getContentDescription()));
                    else if (element instanceof VideoView)
                        listContent.add(new Content(ContentType.VIDEO, (String) ((VideoView)element).getContentDescription()));
                }

                if (listContent.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Du contenu est requis", Toast.LENGTH_SHORT).show();
                } else {
                    Post newPost = new Post(new Date(), topicUser.getTopic(), topicUser.getUser(), listContent, new Date());
                    Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                            intent.putExtra("user",user);
                            startActivity(intent);
                        } else Toast.makeText(AddPostActivity.this, "Erreur lors de la publication du post", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
}