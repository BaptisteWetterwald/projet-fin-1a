package fr.ensisa.ensiblog;

import android.os.Bundle;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.sql.Time;
import java.util.Date;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.*;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.ContentType;
import fr.ensisa.ensiblog.models.posts.ImageContent;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.TextContent;

/**
 * Activity not used in any page of the app. It is just for managing the database before launching the app
 **/
public class DebugActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;

    /**
     * Function called when DebugActivity starts
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Topic ruTopic = new Topic("Resto U", new Role(2));
        Database.getInstance().add(Table.TOPICS.getName(), ruTopic, Topic.class).addOnSuccessListener(aVoid -> {
            Log.i("n6a", "Topic added");
        }).addOnFailureListener(e -> {
            Log.e("n6a", "Error adding topic", e);
        });
        /*
        // Create some TextContent objects
        Content textContent1 = new Content(ContentType.TEXT,"Hello, world!");
        Content textContent2 = new Content(ContentType.TEXT,"This is a sample post.");
        Content textContent3 = new Content(ContentType.TEXT,"Welcome to the Android 21 app.");

        // Create some ImageContent objects
        Content imageContent1 = new Content(ContentType.IMAGE,"https://www.parismatch.com/lmnr/var/pm/public/media/image/Emma-Watson_0.jpg?VersionId=RC8sSswLrmMQFNdbRU7FRE3E80WtYdls");
        Content imageContent2 = new Content(ContentType.IMAGE, "https://www.parismatch.com/lmnr/var/pm/public/media/image/2022/03/01/07/Emma-Watson-son-nouveau-poste-au-sein-d-une-entreprise-francaise.jpg?VersionId=Z4C19TiHw_xvYDNipyHdSIprYGusX1rj");

        Content videoContent1 = new Content(ContentType.VIDEO, "https://joy1.videvo.net/videvo_files/video/free/2014-12/large_watermarked/Metal_Wind_Chimes_at_Sunset_preview.mp4");
*/
        /*Role defaultRole = new Role(2);
        Topic ruTopic = new Topic("Resto U", defaultRole);
        Email email = new Email("emma.watson@uha.fr");

        Post post1 = new Post();
        post1.setCreation(new Date());
        post1.setTopic(ruTopic);
        User author = new User(email);
        author.setPhotoUrl("https://besthqwallpapers.com/Uploads/17-2-2018/41035/thumb2-4k-emma-watson-2018-beautiful-girls-american-actress.jpg");*/



        /*post1.setAuthor(author);

        post1.addContent(textContent1);
        post1.addContent(imageContent1);
        post1.addContent(textContent2);
        post1.addContent(imageContent2);
        post1.addContent(textContent3);
        post1.addContent(videoContent1);

        Database.getInstance().add(Table.POSTS.getName(), post1, Post.class);*/

        /*//TODO: Créer un topicuser dans la DB : TopicUser topicUser = new TopicUser(ruTopic, author, defaultRole);
        TopicUser topicUser = new TopicUser(ruTopic, author, defaultRole);
        topicUser.setFonction("Meuf fraiche");
        Database.getInstance().add(Table.TOPIC_USERS.getName(), topicUser, TopicUser.class).addOnSuccessListener(aVoid -> {
            Log.i("n6a", "TopicUser added");
        }).addOnFailureListener(e -> {
            Log.i("n6a", "TopicUser not added");
        });*/


        /*
        Database.getInstance().get("Topics", Topic.class, new String[]{}, new Object[]{}).addOnSuccessListener(topics -> {
            for (Topic topic : topics) {
                Database.getInstance().add(Table.TOPIC_USERS.getName(), new TopicUser(topic,new User(new Email("arthur.sicard@uha.fr")),new Role(2)), TopicUser.class);
            }
        });

        Topic topic = new Topic("Thème1",new Role(1));
        User user = new User(new Email("test.michelkjenezfkjqjksnfjdsqfnjkjnkqdsjf@uha.fr"));
        TopicUser topicuser = new TopicUser(topic,user,new Role(3));
        Database.getInstance().add(Table.USERS.getName(), user, User.class,true);
        Database.getInstance().add(Table.TOPIC_USERS.getName(), topicuser, TopicUser.class,true);

        Log.i("n6a","Start !");
        for (int i = 0; i < 5; i++) {
            Topic topic = new Topic("Thème"+i,new Role(1));
            Database.getInstance().add(Table.TOPICS.getName(), topic, Topic.class,true);

            for (int j = 0; j < 3; j++) {
                User user = new User(new Email("test.michel"+Integer.toString(j)+"@uha.fr"));
                TopicUser topicuser = new TopicUser(topic,user,new Role(3));
                Database.getInstance().add(Table.USERS.getName(), user, User.class,true);
                Database.getInstance().add(Table.TOPIC_USERS.getName(), topicuser, TopicUser.class,true);
            }
        }

        Database.getInstance().alreadyIn("Topics", new String[]{"name"}, new String[]{"ENSISA"}, new Database.AlreadyInCallback() {
            @Override
            public void onResult(boolean alreadyExists) {
                if (alreadyExists) {
                    // Do something if the topic already exists
                    Log.i("n6a","Topic already exists");
                } else {
                    // Do something if the topic does not exist
                    Log.i("n6a","Topic does not exist");
                }
            }
        });

        Database.getInstance().alreadyIn("Topics", new String[]{"name"}, new String[]{"1234"}, new Database.AlreadyInCallback() {
            @Override
            public void onResult(boolean alreadyExists) {
                if (alreadyExists) {
                    // Do something if the topic already exists
                    Log.i("n6a","Topic already exists");
                } else {
                    // Do something if the topic does not exist
                    Log.i("n6a","Topic does not exist");
                }
            }
        });

        String email = "test.michel@uha.fr";
        String mdp = "2bjbkjbSBHCD%ckd@hdbzj";

        Database.getInstance().add(Table.USERS.getName(), new User(new Email(email)), User.class,true);

        Database.getInstance().add(Table.USERS.getName(), new User(new Email(email)), User.class,false);

        String[] fields = new String[]{"name", "defaultRole"};
        Object[] values = new Object[]{"ENSISA", new Role(0)};

        Database.getInstance().getObjects("Topics", Topic.class, fields, values).addOnSuccessListener(topics -> {
            // Handle the retrieved topics
            System.out.println("Topic size:" + topics.size());
            for (Topic topic : topics) {
                // Process each topic instance
                Log.i("n6a", "Found " + topic.getName());
            }
        }).addOnFailureListener(e -> {
            // Handle any errors that occurred during the query
            Log.i("n6a","Error: " + e.getMessage());
        });
        */

        /*String[] fields = new String[0];
        Object[] values = new Object[0];

        Database.getInstance().get(Table.TOPICS.getName(), Topic.class, fields, values).addOnSuccessListener(topics -> {
            // Handle the retrieved topics
            System.out.println("Topic size:" + topics.size());
            for (Topic topic : topics) {
                // Process each topic instance
                Log.i("n6a", "Found " + topic.getName() + " with default role " + topic.getDefaultRole().getRole());
            }
        }).addOnFailureListener(e -> {
            // Handle any errors that occurred during the query
            Log.i("n6a","Error: " + e.getMessage());
        });*/

        // get topics with name "ENSISA"
        /*Database.getInstance().get(Table.TOPICS.getName(), Topic.class, new String[]{"name"}, new String[]{"ENSISA"}).addOnSuccessListener(topics -> {
            // Handle the retrieved topics
            System.out.println("Topic size:" + topics.size());
            // get the first topic
            Topic topic = topics.get(0);
            Topic old = new Topic(topic);
            topic.setDefaultRole(new Role(99));
            // update the topic
            Database.getInstance().update(Table.TOPICS.getName(), old, topic);
        }).addOnFailureListener(e -> {
            // Handle any errors that occurred during the query
            Log.i("n6a","Error: " + e.getMessage());
        });*/

        //User user = new User(new Email("baptiste.wetterwald@uha.fr"));
        //Database.getInstance().add(Table.USERS.getName(), user, User.class);

        //Authentication auth = new Authentication();
        //String email = "test.michel@uha.fr";
        //String mdp = "2bjbkjbSBHCD%ckd@hdbzj";

        /*auth.createUser(email,mdp).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                String uid = user.getUid();
                Database.getInstance().add(Table.USERS.getName(), new User(new Email(email),uid), User.class).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Log.i("ENSIBLOG","SUCCESSFULLY CREATED USER");
                    } else {
                        Log.i("ENSIBLOG","FAIL TO CREATE USER");
                    }
                });
            } else {
                Log.i("ENSIBLOG","ERREUR : utilisateur existant !");
            }
        });
        auth.signInUser(email,mdp).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               Log.i("ENSISABLOG","SUCCESSFULLY LOGIN");
           } else {
               Log.i("ENSISABLOG","FAIL TO LOGIN");
           }
        });*/

    }

}