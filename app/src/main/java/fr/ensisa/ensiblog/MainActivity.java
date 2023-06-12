package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.removeElement;
import static fr.ensisa.ensiblog.Utils.showInfoBox;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            TextView textUserName = findViewById(R.id.user_name);
            Email usrEmail = new Email(user.getEmail());
            textUserName.setText(usrEmail.firstName() + " " + usrEmail.lastName());
            // On commence par récupérer l'user courant dans notre DB pour filtrer les topics
            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new String[]{user.getUid()}).addOnSuccessListener(users -> {
                if (users.size() > 0) {
                    User userModel = users.get(0);
                    AtomicBoolean isModo = new AtomicBoolean(false);


                    Button buttonModo = (Button) findViewById(R.id.button_moderation);
                    Button buttonAdmin = (Button) findViewById(R.id.button_admin);



                    // On récupère la liste des topics auquel l'user est abonné
                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                        if (topics.size() > 0) {
                            LinearLayout themesBar = findViewById(R.id.main_topics);
                            themesBar.removeAllViews();
                            List<Button> buttons = new ArrayList<>();
                            for (int i = 0; i < topics.size(); i++) {
                                if(topics.get(i).getRole().getRole() >= 3){
                                    isModo.set(true);
                                }
                                Button button = new Button(MainActivity.this);
                                Topic btnTopic = topics.get(i).getTopic();
                                button.setText(btnTopic.getName());
                                if (i == 0) {
                                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                }
                                button.setOnClickListener(v -> {
                                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                    for (Button otherButton : buttons) {
                                        if (otherButton != button) {
                                            otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444"))); // Change to desired color
                                        }
                                    }
                                });
                                themesBar.addView(button);
                                buttons.add(button);
                            }
                            if(isModo.get()){
                                buttonModo.setOnClickListener(v -> {
                                    Intent intent = new Intent(MainActivity.this, ModeratorActivity.class);
                                    intent.putExtra("user",user);
                                    startActivity(intent);
                                });
                            } else removeElement(buttonModo);
                        }
                    });

                    Database.getInstance().alreadyIn(Table.ADMINS.getName(), new String[]{"user"}, new User[]{userModel}, alreadyExists -> {
                        if(!alreadyExists)
                            removeElement(buttonAdmin);
                        else{
                            buttonAdmin.setOnClickListener(v -> {
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                startActivity(intent);
                            });
                        }
                    });
                }
            });

            setSupportActionBar(binding.appBarMain.toolbar);
            /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.name_app)
                    .build();*/

            int colorBlue =getColor(R.color.MARIN_APP);
            String text = getString(R.string.app_name);
            SpannableString spannable = new SpannableString(text);
            // here we set the color
            spannable.setSpan(new ForegroundColorSpan(colorBlue), 0, text.length(), 0);


            Button buttonGestionCompte = (Button) findViewById(R.id.button_gest);

            buttonGestionCompte.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            });


            Button buttonDeco = (Button) findViewById(R.id.button_disconnect);

            buttonDeco.setOnClickListener(v -> {
                new Authentication().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            Button buttonNewPost = (Button) findViewById(R.id.fixedButton);

            buttonNewPost.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(intent);
            });

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationViewleft = binding.leftNavView.leftNavView;


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

            Topic currentTopic = new Topic();
            Database.getInstance().onModif(Table.POSTS.getName(), "Topic",currentTopic,(snapshots, e) -> {
                if (e != null) {
                    Log.w("n6a", "listen:error", e);
                    return;
                }

                assert snapshots != null;
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("n6a", "New : " + dc.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d("n6a", "Modified : " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("n6a", "Removed : " + dc.getDocument().getData());
                            break;
                    }
                }

            });
        }
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