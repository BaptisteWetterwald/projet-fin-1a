package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Date;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // check if a topic with name "ENSISA" already exists
        /*Database.getInstance().alreadyIn("Topics", new String[]{"name"}, new String[]{"ENSISA"}, new AlreadyInListener() {
            @Override
            public void onCheckComplete(boolean exists) {
                if (exists) {
                    // Do something if the topic already exists
                    Log.i("n6a","Topic already exists");
                } else {
                    // Do something if the topic does not exist
                    Log.i("n6a","Topic does not exist");
                }
            }

            @Override
            public void onCheckFailed(Exception e) {
                // Handle any errors that occurred during the query
                Log.i("n6a","Error: " + e.getMessage());
            }
        });*/

        /*
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

    }

}