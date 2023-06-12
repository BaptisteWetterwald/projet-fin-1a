package fr.ensisa.ensiblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
// import org.threeten.bp.LocalDateTime; https://stackoverflow.com/questions/56695997/how-to-fix-call-requires-api-level-26-current-min-is-25-error-in-android
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

        EditText editText = findViewById(R.id.editText);
        Button buttonPublish = findViewById(R.id.buttonPublish);
        Topic topic = null;

/*
        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = editText.getText().toString();
                TextContent content = new TextContent(text);
                List<TextContent> listContent = new ArrayList<TextContent>();
                listContent.add(content);

                if (text.isEmpty()) {
                    Toast.makeText(AddPostActivity.this, "Du contenu est requis", Toast.LENGTH_SHORT).show();
                } else {

                    Post newPost = new Post(Date, topic, listContent, );
                    Database.getInstance().add(Table.POSTS.getName(), newPost, Post.class);
                    /*
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
                    });*//*
                }





            }
        });



/*

        // Pour limiter les appels à la BDD
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            // Topic topic = (Topic) extras.get("topic");
            // On commence par récupérer l'user courant dans notre DB pour filtrer les topics
            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new String[]{user.getUid()}).addOnSuccessListener(users -> {
                if(users.size()>0) {
                    User userModel = users.get(0);
                    // On récupère la liste des topics auquel l'user est abonné
                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                        if(topics.size()>0){
                            TopicUser currentTopic = null;
                            LinearLayout themesBar = findViewById(R.id.theme_bar);
                            themesBar.removeAllViews();
                            List<Button> buttons = new ArrayList<>();
                            for (int i = 0; i < topics.size(); i++) {
                                if(topics.get(i).getRole().getRole() >= 2){

                                    Button button = new Button(AddPostActivity.this);
                                    TopicUser btnTopic = topics.get(i);
                                    button.setText(btnTopic.getTopic().getName());
                                    if(currentTopic == null){
                                        currentTopic = topics.get(i);
                                        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                    }
                                    button.setOnClickListener(v -> {
                                        /*loadMembers(btnTopic)*//*;
                                        topic = btnTopic.getTopic();
                                        button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                        for (Button otherButton : buttons) {
                                            if (otherButton != button) {
                                                otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444"))); // Change to desired color
                                            }
                                        }
                                    });
                                    themesBar.addView(button);
                                    buttons.add(button);

                                }
                            }
                            if(currentTopic != null)*/
                                /*loadMembers(currentTopic);*/
                                /*topic = this.currentTopic;*/
                                /*currentTopic = btnTopic*//*
                                ;
                        } else {
                            Utils.showInfoBox("Warning","No topic founds for your account","OK",AddPostActivity.this,(dialog, which) -> {
                                dialog.cancel();
                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                startActivity(intent);
                            });
                        }
                    });
                } else {
                    Utils.showInfoBox("Warning","No user founds for your account","OK",AddPostActivity.this,(dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                        startActivity(intent);
                    });
                }
            });
        }*/
    }
}