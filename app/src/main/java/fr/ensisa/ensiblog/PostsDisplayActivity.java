package fr.ensisa.ensiblog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import fr.ensisa.ensiblog.models.Content;
import fr.ensisa.ensiblog.models.Image;
import fr.ensisa.ensiblog.models.Post;
import fr.ensisa.ensiblog.models.PostAdapter;
import fr.ensisa.ensiblog.models.Text;
import fr.ensisa.ensiblog.models.Video;

public class PostsDisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_display);

        // Retrieve the reference to the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Create a list of posts (you can replace this with your data retrieval logic)
        List<Post> posts = getPosts();
        Log.i("n6a", "posts: " + posts);

        // Create an instance of the PostAdapter
        adapter = new PostAdapter(posts);

        // Set the adapter for the RecyclerView
        recyclerView.setAdapter(adapter);

        // Set a layout manager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // Method to retrieve the list of posts (replace this with your data retrieval logic)
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
