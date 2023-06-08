package fr.ensisa.ensiblog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    //Topic currentTopic;

    private void loadMembers(Topic currentTopic){
        Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic"}, new Topic[]{currentTopic}).addOnSuccessListener(topicUsers -> {

            if(topicUsers.size()>0) {
                LinearLayout listMembers = findViewById(R.id.list_members);
                listMembers.removeAllViews();
                for (int i = 0; i < topicUsers.size(); i++) {
                    // Inflate the layout XML file for each component
                    View componentView = getLayoutInflater().inflate(R.layout.member_layout, null);

                    // Find the individual views within the inflated componentView
                    TextView usernameTextView = componentView.findViewById(R.id.list_members_text);
                    RadioGroup radioGroup = componentView.findViewById(R.id.list_members_radioGroup);
                    TopicUser tpUsr = topicUsers.get(i);

                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            // Perform actions when the checked radio button changes
                            RadioButton checkedRadioButton = group.findViewById(checkedId);
                            if (checkedRadioButton != null) {
                                // Get the text or any other properties of the selected radio button
                                String selectedText = checkedRadioButton.getText().toString();
                                Role newRole = new Role();

                                if (checkedRadioButton.getId() == R.id.button1)
                                    newRole.setRole(3);
                                else if (checkedRadioButton.getId() == R.id.button2)
                                    newRole.setRole(2);
                                else if (checkedRadioButton.getId() == R.id.button3)
                                    newRole.setRole(1);
                                Database.getInstance().update(Table.TOPIC_USERS.getName(),tpUsr,new TopicUser(tpUsr.getTopic(),tpUsr.getUser(),newRole,tpUsr.getFonction()));
                            }
                        }
                    });


                    Email members_email = tpUsr.getUser().getEmail();
                    // Modify the views as needed
                    usernameTextView.setText(members_email.firstName()+" "+members_email.lastName());
                    RadioButton rd = null;
                    if(tpUsr.getRole().getRole() == 3)
                        rd = componentView.findViewById(R.id.button1);
                    else if (tpUsr.getRole().getRole() == 2)
                        rd = componentView.findViewById(R.id.button2);
                    else if (tpUsr.getRole().getRole() == 1)
                        rd = componentView.findViewById(R.id.button3);
                    if(rd != null)
                        rd.toggle();

                    // Add the componentView to the parent layout
                    listMembers.addView(componentView);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator);

        // Pour limiter les appels à la BDD
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            // user.getUid()
        }
        // On commence par récupérer l'user courant dans notre DB pour filtrer les topics
        Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"email"}, new Email[]{new Email("test.michel19@uha.fr")}).addOnSuccessListener(users -> {
            if(users.size()>0) {
                User userModel = users.get(0);
                // On récupère la liste des topics auquel l'user est abonné
                Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                    if(topics.size()>0){
                        //currentTopic = topics.get(0).getTopic();
                        LinearLayout themesBar = findViewById(R.id.theme_bar);
                        themesBar.removeAllViews();
                        for (int i = 0; i < topics.size(); i++) {
                            Button button = new Button(ModeratorActivity.this);
                            Topic btnTopic = topics.get(i).getTopic();
                            button.setText(btnTopic.getName());
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadMembers(btnTopic);
                                }
                            });
                            themesBar.addView(button);

                        }
                        loadMembers(topics.get(0).getTopic());
                    } else {
                        Utils.showInfoBox("Warning","No topic founds for your account","OK",ModeratorActivity.this,(dialog, which) -> {
                            dialog.cancel();
                            Intent intent = new Intent(ModeratorActivity.this, MainActivity2.class);
                            startActivity(intent);
                        });
                    }
                });
            } else {
                Utils.showInfoBox("Warning","No user founds for your account","OK",ModeratorActivity.this,(dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(ModeratorActivity.this, MainActivity2.class);
                    startActivity(intent);
                });
            }
        });
    }
}
