package fr.ensisa.ensiblog;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.nameapp)
                .build();

/*        Database.getInstance().alreadyIn("Topics", new String[]{"name"}, new String[]{"ENSISA"}, new Database.AlreadyInCallback() {
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