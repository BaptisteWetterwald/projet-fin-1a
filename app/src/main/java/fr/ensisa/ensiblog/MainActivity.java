package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.showInfoBox;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import java.util.Date;
import java.util.List;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.User;
import fr.ensisa.ensiblog.models.posts.ImageContent;
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.VideoContent;
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
        Button button5 = (Button) findViewById(R.id.fixedButton);
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
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            }
        });


        //Left menu Controller
        mAppBarConfigurationLeft = new AppBarConfiguration.Builder(R.id.nav_home).setOpenableLayout(drawer).build();
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

        Database.getInstance().get(Table.TOPICS.getName(), Topic.class, new String[]{}, new Topic[]{}).addOnSuccessListener(topics -> {
            if (topics.size() > 0) {
                LinearLayout left_view = findViewById(R.id.left_scroll);
                left_view.removeAllViews();
                List<Button> buttons = new ArrayList<>();
                for (int i = 0; i < topics.size(); i++) {
                    Button button = new Button(this);
                    Topic btnTopic = topics.get(i);
                    button.setText(btnTopic.getName());
                    button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    left_view.addView(button);
                    buttons.add(button);
                }
            }
        });
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
        ImageContent imageContent2 = new ImageContent("https://www.parismatch.com/lmnr/var/pm/public/media/image/2022/03/01/07/Emma-Watson-son-nouveau-poste-au-sein-d-une-entreprise-francaise.jpg?VersionId=Z4C19TiHw_xvYDNipyHdSIprYGusX1rj");

        // Create some VideoContent objects
        VideoContent videoContent1 = new VideoContent("https://joy1.videvo.net/videvo_files/video/free/2013-09/large_watermarked/AbstractRotatingCubesVidevo_preview.mp4");

        for (int i=0; i<5; i++){
            // Create some posts with different combinations of content
            Role defaultRole = new Role(2);
            Topic ruTopic = new Topic("Resto U", defaultRole);
            Email email = new Email("baptiste.wetterwald@gmail.com");

            Post post1 = new Post();
            post1.setCreation(new Date());
            post1.setTopic(ruTopic);
            post1.setAuthor(new User(email));

            post1.addContent(imageContent1);
            post1.addContent(textContent1);
            post1.addContent(textContent2);
            post1.addContent(imageContent2);
            post1.addContent(textContent3);

            Topic muscuTopic = new Topic("Muscu", defaultRole);

            Post post2 = new Post();
            post2.setCreation(new Date());
            post2.setTopic(muscuTopic);
            Email email2 = new Email("ayoub.tazi-chibi@uha.fr");
            post2.setAuthor(new User(email2));
            post2.addContent(new TextContent("There should be a video below this text."));
            post2.addContent(videoContent1);

            posts.add(post1);
            posts.add(post2);
        }

        return posts;
    }


}