package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
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

        Database.getInstance().getTopic("ENSISA", new TopicListener() {
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
        });

    }

}