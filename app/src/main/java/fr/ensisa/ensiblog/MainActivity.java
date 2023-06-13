package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.removeElement;
import static fr.ensisa.ensiblog.Utils.showInfoBox;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
import fr.ensisa.ensiblog.models.posts.Post;
import fr.ensisa.ensiblog.models.posts.PostWithFunction;
import fr.ensisa.ensiblog.ui.posts.PostWithFunctionAdapter;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfigurationLeft;

    private List<PostWithFunction> postsWithFunctions = new ArrayList<>();
    private PostWithFunctionAdapter adapter;

    private QuerySnapshot postsSnapshot;

    private TopicUser currentTopicUser = null;

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
                                TopicUser btnTopic = topics.get(i);
                                button.setText(btnTopic.getTopic().getName());
                                if (i == 0) {
                                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                    currentTopicUser = btnTopic;
                                }
                                button.setOnClickListener(v -> {
                                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                    for (Button otherButton : buttons) {
                                        if (otherButton != button) {
                                            otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444"))); // Change to desired color
                                        }
                                    }
                                    currentTopicUser = btnTopic;
                                    loadAllPosts();
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
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.nameapp)
                    .build();

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
                intent.putExtra("user",user);
                intent.putExtra("topicUser",currentTopicUser);
                startActivity(intent);
            });

            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationViewleft = binding.leftNavView.leftNavViewPane;

            //Left menu Controller
            mAppBarConfigurationLeft = new AppBarConfiguration.Builder(R.id.nav_home).setOpenableLayout(drawer).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfigurationLeft);
            NavigationUI.setupWithNavController(navigationViewleft, navController);

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            loadAllPosts();
            // Create an instance of the PostAdapter
            adapter = new PostWithFunctionAdapter(postsWithFunctions);
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfigurationLeft)
                || super.onSupportNavigateUp();
    }

    private void loadAllPosts() {
        postsWithFunctions = new ArrayList<>();

        Topic currentTopic = new Topic("Resto U", new Role(2));

        // Get all existing posts once
        Database.getInstance().onModif(Table.POSTS.getName(), "topic", currentTopic, (snapshots, e) -> {
            if (e != null) {
                Log.w("n6a", "listen:error", e);
                return;
            }

            assert snapshots != null;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                Post post = dc.getDocument().toObject(Post.class);
                PostWithFunction postWithFunction = new PostWithFunction(post, null);
                switch (dc.getType()) {
                    case ADDED:
                        // Get the function of the author for the topic
                        Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic", "user"}, new Object[]{post.getTopic(), post.getAuthor()}).addOnSuccessListener(topicUsers -> {
                            if (topicUsers.size() > 0) {
                                postWithFunction.setFunction(topicUsers.get(0).getFonction());
                            }
                            Log.d("n6a", "New : " + postWithFunction);
                            postsWithFunctions.add(postWithFunction);
                            adapter.notifyItemInserted(postsWithFunctions.size() - 1);
                        }).addOnFailureListener(e1 -> Log.w("n6a", "Error getting documents.", e1));
                        break;
                    case MODIFIED:
                        Log.d("n6a", "Modified : " + postWithFunction);
                        // Handle modified posts if needed
                        break;
                    case REMOVED:
                        Log.d("n6a", "Removed : " + postWithFunction);
                        int index = postsWithFunctions.indexOf(postWithFunction);
                        Log.i("n6a", "Index : " + index);
                        postsWithFunctions.remove(postWithFunction);
                        adapter.notifyItemRemoved(index);
                        break;
                }
            }

            // Notify the adapter that the data has changed
            //adapter.notifyDataSetChanged();
        });
    }


}