package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.AlreadyInListener;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.TopicListener;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;

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
        //Database.getInstance().addTopic(new Topic("ENSISA", new Role(0)));
        //Topic topic = Database.getInstance().getTopic("ENSISA");

        /*Database.getInstance().getTopic("ENSISA", new TopicListener() {
            @Override
            public void onTopicRetrieved(Topic topic) {
                if (topic != null) {
                    // Do something with the retrieved topic
                    Log.i("n6a", topic.getName());
                } else {
                    // Handle the case when no topic is found
                    Log.i("n6a","No topic found");
                }
            }

            @Override
            public void onTopicRetrievalFailed(Exception e) {
                // Handle any errors that occurred during the query
                Log.i("n6a","Error: " + e.getMessage());
            }
        });*/

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

        String[] fields = new String[]{"name", "defaultRole"};
        Object[] values = new Object[]{"ENSISA", new Role(0)};

        Database.getInstance().getObjects("Topics", Topic.class, fields, values).addOnSuccessListener(new OnSuccessListener<List<Topic>>() {
            @Override
            public void onSuccess(List<Topic> topics) {
                // Handle the retrieved topics
                for (Topic topic : topics) {
                    // Process each topic instance
                    Log.i("n6a", topic.getName());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occurred during the query
            }
        });


    }

}