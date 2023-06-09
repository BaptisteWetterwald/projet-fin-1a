package fr.ensisa.ensiblog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.models.posts.ImageContent;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.ui.posts.PostAdapter;
import fr.ensisa.ensiblog.models.posts.TextContent;


public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfigurationLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.nameapp)
                .build();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationViewleft = binding.leftNavView.leftNavView;
        Button button1 = (Button) findViewById(R.id.button_gest);
        Button button2 = (Button) findViewById(R.id.button_moderation);
        Button button3 = (Button) findViewById(R.id.button_admin);
        Button button4 = (Button) findViewById(R.id.button_disconnect);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ModeratorActivity.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //Left menu Controller
        mAppBarConfigurationLeft = new AppBarConfiguration.Builder(
                R.id.nav_home).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfigurationLeft);
        NavigationUI.setupWithNavController(navigationViewleft, navController);



        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        // Create a list of posts (you can replace this with your data retrieval logic)
        List<Post> posts = getPosts();
        Log.i("n6a", "posts: " + posts);
        // Create an instance of the PostAdapter
        PostAdapter adapter = new PostAdapter(posts);
        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);
        // Set a layout manager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfigurationLeft)
                || super.onSupportNavigateUp();
    }

    public static List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();

        // Create some TextContent objects
        TextContent textContent1 = new TextContent("Hello, world!");
        TextContent textContent2 = new TextContent("This is a sample post.");
        TextContent textContent3 = new TextContent("Welcome to the Android 21 app.");

        // Create some ImageContent objects
        ImageContent imageContent1 = new ImageContent("https://www.parismatch.com/lmnr/var/pm/public/media/image/Emma-Watson_0.jpg?VersionId=RC8sSswLrmMQFNdbRU7FRE3E80WtYdls");
        /*ImageContent imageContent2 = new ImageContent("https://example.com/image2.jpg");

        // Create some VideoContent objects
        VideoContent videoContent1 = new VideoContent("https://example.com/video1.mp4");
        VideoContent videoContent2 = new VideoContent("https://example.com/video2.mp4");*/

        // Create some posts with different combinations of content
        Post post1 = new Post();
        post1.addContent(textContent1);
        post1.addContent(textContent2);
        post1.addContent(textContent3);
        post1.addContent(imageContent1);

        /*Post post2 = new Post();
        post2.addContent(textContent2);

        Post post3 = new Post();
        post3.addContent(imageContent2);
        post3.addContent(videoContent1);
        post3.addContent(textContent3);

        Post post4 = new Post();
        post4.addContent(videoContent2);*/

        // Add the posts to the list
        posts.add(post1);
        //posts.add(post2);
        //posts.add(post3);
        //posts.add(post4);

        return posts;
    }


}