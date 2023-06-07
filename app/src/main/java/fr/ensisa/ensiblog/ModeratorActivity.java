package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;


public class ModeratorActivity extends AppCompatActivity {

    Topic currentTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        // Pour limiter les appels à la BDD
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            // On commence par récupérer l'user courant dans notre DB pour filtrer les topics
            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new String[]{user.getUid()}).addOnSuccessListener(users -> {
                User userModel = users.get(0);
                // On récupère la liste des topics auquel l'user est abonné
                Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                    if(topics.size()>0){
                        currentTopic = topics.get(0).getTopic();
                        LinearLayout themesBar = findViewById(R.id.theme_bar);
                        for (int i = 0; i < topics.size(); i++) {
                            Button button = new Button(ModeratorActivity.this);
                            button.setText(topics.get(i).getTopic().getName());
                            themesBar.addView(button);
                        }

                        Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic"}, new Topic[]{currentTopic}).addOnSuccessListener(topicUsers -> {

                            if(topicUsers.size()>0) {
                                LinearLayout listMembers = findViewById(R.id.list_members);

                                for (int i = 0; i < topicUsers.size(); i++) {
                                    // Inflate the layout XML file for each component
                                    View componentView = getLayoutInflater().inflate(R.layout.member_layout, null);

                                    // Find the individual views within the inflated componentView
                                    TextView usernameTextView = componentView.findViewById(R.id.list_members_text);
                                    RadioGroup radioGroup = componentView.findViewById(R.id.list_members_radioGroup);

                                    Email members_email = topics.get(i).getUser().getEmail();
                                    // Modify the views as needed
                                    usernameTextView.setText(members_email.firstName()+" "+members_email.lastName());

                                    // Add the componentView to the parent layout
                                    listMembers.addView(componentView);
                                }
                            }
                        });
                    }



                });
            });
        }
    }
}
