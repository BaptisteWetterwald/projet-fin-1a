package fr.ensisa.ensiblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

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
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;

public class AddPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        LinearLayout list_content = findViewById(R.id.list_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            TextView userName = findViewById(R.id.textViewUsername);
            Email usrEmail = new Email(user.getEmail());
            userName.setText(usrEmail.firstName() + " " + usrEmail.lastName());

            Button addImage = findViewById(R.id.buttonAddImage);
            Button addText = findViewById(R.id.buttonAddText);
            Button addVideo = findViewById(R.id.buttonAddVideo);

            addImage.setOnClickListener(click -> {
                ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                        registerForActivityResult(new PickVisualMedia(), uri -> {
                            // Callback is invoked after the user selects a media item or closes the
                            // photo picker.
                            if (uri != null) {
                                Log.d("PhotoPicker", "Selected URI: " + uri);
                            } else {
                                Log.d("PhotoPicker", "No media selected");
                            }
                        });
                ImageView imgView = new ImageView(AddPostActivity.this);
                Picasso.get().load(Uri.parse(content.getData())).into(imgView);
                list_content.addView(imgView);
            });

            Button buttonPublish = findViewById(R.id.buttonPublish);
            Topic topic = null;



        }

        /*buttonPublish.setOnClickListener(v -> {

            String text = editText.getText().toString();
            TextContent content = new TextContent(text);
            List<TextContent> listContent = new ArrayList<TextContent>();
            listContent.add(content);

            if (text.isEmpty()) {
                Toast.makeText(AddPostActivity.this, "Du contenu est requis", Toast.LENGTH_SHORT).show();
            } else {

                Post newPost = new Post(Date, topic, listContent, );
                Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class);

                Topic newTopic = new Topic(themeName,new Role(selectedRole));
                Database.getInstance().add(Table.TOPICS.getName(), newTopic, Topic.class,new String[]{"name"});
                Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"email"}, new Email[]{new Email(moderatorEmail)}).addOnSuccessListener(users -> {
                    if(users.size()>0) {
                        Database.getInstance().add(Table.TOPIC_USERS.getName(), new TopicUser(newTopic, users.get(0), new Role(4)), TopicUser.class, true);

                        // On affiche le thème créé à la vue
                        themes.add(newTopic);
                        adapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(listView.getCount() - 1);
                    } else {
                        Utils.showInfoBox("Alert !","User not found with email","OK",AdminActivity.this, (dialog2, which2) -> {
                            dialog2.cancel();
                        });
                    }
                });
            }
        });*/

    }
}