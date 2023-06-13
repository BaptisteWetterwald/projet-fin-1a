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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

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
    private Boolean already_exist;
    private User userModel;
    private List<Button> buttons = new ArrayList<>();


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
                    userModel = users.get(0);
                    AtomicBoolean isModo = new AtomicBoolean(false);

                    Button buttonModo = (Button) findViewById(R.id.button_moderation);
                    Button buttonAdmin = (Button) findViewById(R.id.button_admin);

                    // On récupère la liste des topics auquel l'user est abonné
                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics -> {
                        if (topics.size() > 0) {
                            LinearLayout themesBar = findViewById(R.id.main_topics);
                            themesBar.removeAllViews();
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
            /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.name_app)
                    .build();*/

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
            get_left_view();
        }
    }
    private void get_left_view(){
        Database.getInstance().get(Table.TOPICS.getName(), Topic.class, new String[]{}, new Topic[]{}).addOnSuccessListener(topics_1 -> {
            LinearLayout left_view = findViewById(R.id.left_scroll);
            left_view.removeAllViews();
            Log.i("n6a",userModel.getEmail().getAddress());
            Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topics_user -> {
                Log.i("n6a","nombre de topic_user : "+topics_user.size());
                for (int j=0;j<topics_user.size();j++){
                    ToggleButton button = new ToggleButton(this);
                    TopicUser btnTopic = topics_user.get(j);
                    button.setTextOn(btnTopic.getTopic().getName());
                    button.setTextOff(btnTopic.getTopic().getName());
                    button.setText(btnTopic.getTopic().getName());
                    button.setChecked(true);
                    button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    button.setOnClickListener(v -> {
                        showInfoBox("Warning", "Se désabonner de " + btnTopic.getTopic().getName() + " ?", "OK","Annuler", this, (dialog, which) -> {
                            dialog.cancel();
                            Database.getInstance().removeFrom(Table.TOPIC_USERS.getName(), new String[]{"user","topic"}, new Object[]{userModel,btnTopic.getTopic()});
                            LinearLayout themesBar = findViewById(R.id.main_topics);
                            for (int i = 0; i < themesBar.getChildCount(); i++) {
                                View childView = themesBar.getChildAt(i);
                                // Vérifier si la vue est un bouton et si le texte correspond
                                if (childView instanceof Button) {
                                    Button button_del = (Button) childView;
                                    if (button_del.getText().toString().equals(btnTopic.getTopic().getName())) {
                                        themesBar.removeView(button_del);
                                        break; // Quitter la boucle après avoir supprimé le bouton
                                    }
                                }
                            }
                            get_left_view();
                        },(dialog2, which)->{dialog2.cancel();button.setChecked(true);});
                    });
                    left_view.addView(button);
                }
                for (int i = 0; i < topics_1.size(); i++) {
                    already_exist = false;
                    for (int u = 0; u < topics_user.size(); u++) {
                        if (topics_1.get(i).getName().equals(topics_user.get(u).getTopic().getName())) {
                            already_exist = true;
                            break;
                        }else{continue;}
                    }
                    if (already_exist == false) {
                        ToggleButton button = new ToggleButton(this);
                        Topic btnTopic = topics_1.get(i);
                        button.setText(btnTopic.getName());
                        button.setTextOn(btnTopic.getName());
                        button.setTextOff(btnTopic.getName());
                        button.setChecked(false);
                        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        button.setOnClickListener(v -> {
                            showInfoBox("Warning", "S'abonner à " + btnTopic.getName() + " ?", "OK","Annuler", this, (dialog, which) -> {
                                dialog.cancel();
                                Topic topic = new Topic(btnTopic.getName(), new Role(1));
                                User user_left = new User(userModel.getEmail(), userModel.getUid());
                                TopicUser topic_user = new TopicUser(topic, user_left, new Role(1));
                                Database.getInstance().add(Table.TOPIC_USERS.getName(), topic_user, TopicUser.class, false);
                                Button button_tp = new Button(MainActivity.this);
                                button_tp.setText(btnTopic.getName());
                                LinearLayout themesBar = findViewById(R.id.main_topics);
                                button_tp.setOnClickListener(x -> {
                                    button_tp.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                                    for (Button otherButton : buttons) {
                                        if (otherButton != button_tp) {
                                            otherButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444"))); // Change to desired color
                                        }
                                    }
                                });
                                themesBar.addView(button_tp);
                                buttons.add(button_tp);
                                get_left_view();
                            },(dialog2, which)->{dialog2.cancel();button.setChecked(false);});
                        });
                        left_view.addView(button);

                    }else{continue;}
                }
            });

        });
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