package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.showInfoBox;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.ensisa.ensiblog.databinding.ActivityMainBinding;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
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
    private List<Topic> displayedTopics = new ArrayList<>();
    private Map<Topic,ListenerRegistration> topicsRegistered = new HashMap<>();
    private void removeListener(Button btn){
        btn.setOnClickListener(null);
        for (Topic t : displayedTopics)
            if (t.getName() == btn.getText()){
                Objects.requireNonNull(topicsRegistered.get(t)).remove();
                break;
            }
    }
    private void addListener(Button button,Topic btnTopic){
        button.setOnClickListener(v -> {
            // check if displayedTopics contains a topic with name button.getText()
            boolean contains = false;
            for (Topic topic : displayedTopics) {
                if (topic.getName().contentEquals(button.getText())) {
                    contains = true;
                    displayedTopics.remove(topic);
                    Objects.requireNonNull(topicsRegistered.get(topic)).remove();
                    List<PostWithFunction> remove = new ArrayList<>();
                    for (int i=0;i< postsWithFunctions.size();i++) {
                        if(postsWithFunctions.get(i).getPost().getTopic().equals(btnTopic)){
                            remove.add(postsWithFunctions.get(i));
                        }
                    }
                    for (PostWithFunction ps: remove) {
                        postsWithFunctions.remove(ps);
                    }
                    // sort the list by date of creation of the post for api 21
                    Collections.sort(postsWithFunctions, (o1, o2) -> o2.getPost().getCreation().compareTo(o1.getPost().getCreation()));
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if (!contains) {
                displayedTopics.add(btnTopic);
                topicsRegistered.put(btnTopic,
                    Database.getInstance().onModif(Table.POSTS.getName(), "topic", btnTopic, (snapshots, e) -> {
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
                                        Collections.sort(postsWithFunctions, (o1, o2) -> o2.getPost().getCreation().compareTo(o1.getPost().getCreation()));
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
                                    Collections.sort(postsWithFunctions, (o1, o2) -> o2.getPost().getCreation().compareTo(o1.getPost().getCreation()));
                                    adapter.notifyItemRemoved(index);
                                    break;
                            }
                        }

                        // Notify the adapter that the data has changed
                        //adapter.notifyDataSetChanged();
                    })
                );
                //return;
            }
            button.setBackgroundTintList(displayedTopics.contains(btnTopic) ? ColorStateList.valueOf(Color.parseColor("#539AC1")) : ColorStateList.valueOf(Color.parseColor("#444444")));

            Collections.sort(postsWithFunctions, (o1, o2) -> o2.getPost().getCreation().compareTo(o1.getPost().getCreation()));
            adapter.notifyDataSetChanged();

        });
    }


    private MaterialButton createButton(Context context, String buttonText) {
        MaterialButton button = new MaterialButton(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100
        );
        params.setMargins(10, 20, 10, 20); // Set margins for the buttons
        button.setLayoutParams(params);

        Drawable background = getResources().getDrawable(R.drawable.btn_bg); // Set the background drawable for the button
        button.setBackground(background);

        button.setTextColor(Color.WHITE); // Set the text color for the button
        button.setText(buttonText); // Set the text for the button

        return button;
    }


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

            LinearLayout buttonsLayout = findViewById(R.id.buttons_right_panel);

            // On commence par récupérer l'user courant dans notre DB pour filtrer les topics
            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new String[]{user.getUid()}).addOnSuccessListener(users -> {
                if (users.size() > 0) {
                    userModel = users.get(0);
                    if(userModel.getPhotoUrl() != null){
                        ImageView avatar = findViewById(R.id.img_avatar);
                        Picasso.get().load(userModel.getPhotoUrl()).into(avatar);
                    }

                    get_left_view();
                    AtomicBoolean isModo = new AtomicBoolean(false);

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

                                addListener(button,btnTopic.getTopic());

                                themesBar.addView(button);
                                buttons.add(button);
                                // Par défaut on va cocher tous les thèmes auquel on est abonné
                                button.performClick();
                            }
                            if(isModo.get()){
                                MaterialButton buttonModo = createButton(this, "Modération");
                                buttonsLayout.addView(buttonModo);
                                buttonModo.setOnClickListener(v -> {
                                    Intent intent = new Intent(MainActivity.this, ModeratorActivity.class);
                                    intent.putExtra("user",user);
                                    startActivity(intent);
                                });
                            }
                        }
                    });


                    Database.getInstance().alreadyIn(Table.ADMINS.getName(), new String[]{"email"}, new Email[]{userModel.getEmail()}, alreadyExists -> {
                        if(alreadyExists) {
                            MaterialButton buttonAdmin = createButton(this, "Administration");
                            buttonsLayout.addView(buttonAdmin);
                            buttonAdmin.setOnClickListener(v -> {
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                startActivity(intent);
                            });
                        }
                    });

                    MaterialButton buttonGestionCompte = createButton(this, "Gestion Compte");
                    buttonsLayout.addView(buttonGestionCompte);
                    buttonGestionCompte.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                        intent.putExtra("user",user);
                        intent.putExtra("User",userModel);
                        startActivity(intent);
                    });

                    Button buttonNewPost = (Button) findViewById(R.id.fixedButton);
                    buttonNewPost.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                        intent.putExtra("user",user);
                        intent.putExtra("User",userModel);
                        startActivity(intent);
                    });
                }
            });
            setSupportActionBar(binding.appBarMain.toolbar);

            Button buttonDeco = (Button) findViewById(R.id.button_disconnect);
            buttonDeco.setOnClickListener(v -> {
                new Authentication().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationViewleft = binding.leftNavView.leftNavViewPane;
            ((Button)findViewById(R.id.refresh_button)).setOnClickListener(click -> get_left_view());
            //Left menu Controller
            mAppBarConfigurationLeft = new AppBarConfiguration.Builder(R.id.nav_home).setOpenableLayout(drawer).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfigurationLeft);
            NavigationUI.setupWithNavController(navigationViewleft, navController);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            // Create an instance of the PostAdapter
            adapter = new PostWithFunctionAdapter(postsWithFunctions);
            // Set the adapter for the RecyclerView
            recyclerView.setAdapter(adapter);
            // Set a layout manager for the RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DisplayBio();

    }
    private void get_left_view(){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Center horizontally within the parent
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.topMargin = 20;
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
                    button.setWidth(500);
                    button.setTextOff(btnTopic.getTopic().getName());
                    button.setText(btnTopic.getTopic().getName());
                    button.setChecked(true);

                    button.setLayoutParams(layoutParams);
                    button.setOnClickListener(v -> {
                        showInfoBox("Warning", "Se désabonner de " + btnTopic.getTopic().getName() + " ?", "OK","Annuler", this, (dialog, which) -> {
                            dialog.cancel();
                            button.setWidth(500);
                            Database.getInstance().removeFrom(Table.TOPIC_USERS.getName(), new String[]{"user","topic"}, new Object[]{userModel,btnTopic.getTopic()});
                            LinearLayout themesBar = findViewById(R.id.main_topics);
                            for (int i = 0; i < themesBar.getChildCount(); i++) {
                                View childView = themesBar.getChildAt(i);
                                // Vérifier si la vue est un bouton et si le texte correspond
                                if (childView instanceof Button) {
                                    Button button_del = (Button) childView;
                                    if (button_del.getText().toString().equals(btnTopic.getTopic().getName())) {
                                        themesBar.removeView(button_del);
                                        buttons.remove(button_del);
                                        topicsRegistered.remove(btnTopic.getTopic());
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
                        }
                    }
                    if (!already_exist) {
                        ToggleButton button = new ToggleButton(this);
                        Topic btnTopic = topics_1.get(i);
                        button.setText(btnTopic.getName());
                        button.setWidth(500);
                        button.setTextOn(btnTopic.getName());
                        button.setTextOff(btnTopic.getName());
                        button.setChecked(false);
                        button.setLayoutParams(layoutParams);
                        button.setOnClickListener(v -> {
                            showInfoBox("Warning", "S'abonner à " + btnTopic.getName() + " ?", "OK","Annuler", this, (dialog, which) -> {
                                dialog.cancel();
                                Database.getInstance().get(Table.TOPICS.getName(), Topic.class, new String[]{"name"}, new String[]{btnTopic.getName()}).addOnSuccessListener(topics -> {
                                    if(topics.size()>0){
                                        TopicUser topicUser = new TopicUser(topics.get(0),userModel,topics.get(0).getDefaultRole());
                                        Database.getInstance().add(Table.TOPIC_USERS.getName(), topicUser, TopicUser.class, false);
                                        Button button_tp = new Button(MainActivity.this);
                                        button_tp.setText(btnTopic.getName());
                                        LinearLayout themesBar = findViewById(R.id.main_topics);
                                        addListener(button_tp,topics.get(0));
                                        themesBar.addView(button_tp);
                                        buttons.add(button_tp);
                                        get_left_view();
                                    } else Toast.makeText(MainActivity.this,"User not in topic",Toast.LENGTH_SHORT).show();
                                });
                            },(dialog2, which)->{dialog2.cancel();button.setChecked(false);});
                        });
                        left_view.addView(button);
                    }
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
    private void DisplayBio() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userUid = user.getUid();
        Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{}, new String[]{})
                .addOnSuccessListener(userList -> {
                    for (User user1 : userList) {
                        if (user1.getUid() != null) {
                            if (user1.getUid().equals(userUid)) {
                                String currentBio = user1.getBiographie();
                                TextView editTextBio = findViewById(R.id.editTextBio1);
                                editTextBio.setText(currentBio);
                            }
                        }
                    }
                });
    }
}