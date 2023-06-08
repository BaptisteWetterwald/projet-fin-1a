package fr.ensisa.ensiblog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ensisa.ensiblog.databinding.ActivityMain2Binding;
import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.models.Content;
import fr.ensisa.ensiblog.models.Image;
import fr.ensisa.ensiblog.models.Post;
import fr.ensisa.ensiblog.models.PostAdapter;
import fr.ensisa.ensiblog.models.Text;


public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfigurationLeft;
    private AppBarConfiguration mAppBarConfigurationRight;


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
        NavigationView navigationViewright = binding.rightNavView.navRightView;

        mAppBarConfigurationLeft = new AppBarConfiguration.Builder(
                R.id.nav_home/*, R.id.nav_gallery, R.id.nav_slideshow*/)
                .setOpenableLayout(drawer)
                .build();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfigurationLeft)
                || super.onSupportNavigateUp();
    }

    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // Add some sample posts
            Post post1 = new Post();
            Text textContent;
            try {
                textContent = new Text("This is a sample text post (i: " + i + ")");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<Content> contents = new ArrayList<>();
            contents.add(textContent);
            post1.setContent(contents);
            posts.add(post1);

            Post post2 = new Post();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ensisa);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Image imageContent;
            try {
                imageContent = new Image(byteArray);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            contents = new ArrayList<>();
            contents.add(imageContent);
            post2.setContent(contents);
            posts.add(post2);
        }
    
        return posts;
    }


}