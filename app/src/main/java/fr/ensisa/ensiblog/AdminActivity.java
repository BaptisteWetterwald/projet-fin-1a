package fr.ensisa.ensiblog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;
import fr.ensisa.ensiblog.models.posts.Content;
import fr.ensisa.ensiblog.models.posts.Post;

/**
 * Activity used on the admin page of the application
 **/
public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<Topic> adapter;

    private ArrayList<Topic> themes = new ArrayList<Topic>();

    /**
     * Function called when AdminActivity starts
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listView);
        Button newThemeButton = findViewById(R.id.newThemeButton);

        // listener for New Theme button
        newThemeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Nouveau Thème");

            LinearLayout layout = new LinearLayout(AdminActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            EditText themeNameEditText = new EditText(AdminActivity.this);
            themeNameEditText.setHint("Nom du Thème");
            layout.addView(themeNameEditText);

            EditText moderatorNameEditText = new EditText(AdminActivity.this);
            moderatorNameEditText.setHint("Nom du Super-Modérateur");
            layout.addView(moderatorNameEditText);

            // Création du TextView pour la description des boutons radios
            TextView descriptionTextView = new TextView(AdminActivity.this);
            descriptionTextView.setText("Rôles par défaut");
            layout.addView(descriptionTextView);

            RadioGroup roleRadioGroup = new RadioGroup(AdminActivity.this);
            roleRadioGroup.setOrientation(LinearLayout.VERTICAL);

            RadioButton readerRadioButton = new RadioButton(AdminActivity.this);
            readerRadioButton.setText(R.string.baserole_reader);
            roleRadioGroup.addView(readerRadioButton);

            RadioButton editorRadioButton = new RadioButton(AdminActivity.this);
            editorRadioButton.setText(R.string.baserole_editor);
            roleRadioGroup.addView(editorRadioButton);

            readerRadioButton.setChecked(true);

            layout.addView(roleRadioGroup);

            builder.setView(layout);

            builder.setPositiveButton("Valider", (dialog, which) -> {
                String themeName = themeNameEditText.getText().toString();
                String moderatorEmail = moderatorNameEditText.getText().toString();
                int selectedRole = readerRadioButton.isChecked() ? 1 : 2;
                if (themeName.isEmpty()) {
                    Toast.makeText(AdminActivity.this, "Un nom de thème est requis", Toast.LENGTH_SHORT).show();
                } else if (moderatorEmail.isEmpty() && Email.isValid(moderatorEmail)) {
                    Toast.makeText(AdminActivity.this, "Un mail correct est requis", Toast.LENGTH_SHORT).show();
                } else {
                    Topic newTopic = new Topic(themeName,new Role(selectedRole));
                    Database.getInstance().add(Table.TOPICS.getName(), newTopic, Topic.class, new String[]{"name"});
                    Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"email"}, new Email[]{new Email(moderatorEmail)}).addOnSuccessListener(users -> {
                        if(users.size()>0) {
                            Database.getInstance().add(Table.TOPIC_USERS.getName(), new TopicUser(newTopic, users.get(0), new Role(4)), TopicUser.class);

                            // On affiche le thème créé à la vue
                            themes.add(newTopic);
                            adapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(listView.getCount() - 1);
                        } else {
                            Utils.showInfoBox("Alert !","User not found with email","OK",AdminActivity.this, (dialog2, which2) -> {
                                dialog2.cancel();
                            });
                        }
                    });
                }
            });
            //.addOnSuccessListener(o -> Toast.makeText(AdminActivity.this,"Topic successfully created !",Toast.LENGTH_SHORT))
            builder.setNegativeButton("Annuler", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        Database.getInstance().get(Table.TOPICS.getName(), Topic.class,new String[]{},new Object[]{}).addOnSuccessListener(topics -> {

            for (int i = 0; i < topics.size(); i++) {
                themes.add(topics.get(i));
            }
            adapter = new ArrayAdapter<Topic>(AdminActivity.this, R.layout.list_item, R.id.itemTextView, themes) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View view = super.getView(position, convertView, parent);
                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic"}, new Topic[]{themes.get(position)}).addOnSuccessListener(topicUsers -> {

                        TextView themeTitle = view.findViewById(R.id.itemTextView);
                        themeTitle.setText(themes.get(position).getName());

                        TextView moderatorName = view.findViewById(R.id.moderatorNameTextView);
                        for (TopicUser tu : topicUsers) {
                            if (tu.getRole().getRole() == 4) {
                                moderatorName.setText(tu.getUser().getEmail().firstName() + " " + tu.getUser().getEmail().lastName());
                                break;
                            }
                        }
                        Button editButton = view.findViewById(R.id.editButton);
                        Button deleteButton = view.findViewById(R.id.deleteButton);
                        Button changeModeratorButton = view.findViewById(R.id.changeModeratorButton);

                        // listener for the Theme Name Edit button
                        editButton.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                            builder.setTitle("Changer nom du thème");
                            builder.setMessage("Entrez le nouveau nom du thème");

                            final EditText input = new EditText(AdminActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            input.setLayoutParams(layoutParams);
                            builder.setView(input);

                            builder.setPositiveButton("Valider", (dialog, which) -> {
                                String newThemeName = input.getText().toString();
                                if (newThemeName.isEmpty()) {
                                    Toast.makeText(AdminActivity.this, "Un nom de thème est requis", Toast.LENGTH_SHORT).show();
                                } else {
                                    Database.getInstance().alreadyIn(Table.TOPICS.getName(), new String[]{"name"}, new String[]{newThemeName}, alreadyExists -> {
                                        if (alreadyExists) {
                                            Toast.makeText(AdminActivity.this, "Thème déjà existant", Toast.LENGTH_SHORT).show();
                                        } else {
                                            int pos = listView.getPositionForView(v);
                                            Topic topic = adapter.getItem(pos);
                                            Topic newTopic = new Topic(newThemeName, topic.getDefaultRole());
                                            Database.getInstance().update(Table.TOPICS.getName(), newTopic, new String[]{"name"}, new String[]{topic.getName()});
                                            Database.getInstance().get(Table.POSTS.getName(), Post.class, new String[]{"topic"}, new Topic[]{topic}).addOnSuccessListener(posts -> {
                                                for (Post post: posts) {
                                                    post.setTopic(newTopic);
                                                    Database.getInstance().update(Table.POSTS.getName(), post, new String[]{"creation","content"}, new Object[]{post.getCreation(),post.getContent()});
                                                }
                                                TextView themeName = ((LinearLayout) v.getParent().getParent()).findViewById(R.id.itemTextView);
                                                themeName.setText(newThemeName);
                                            });

                                        }
                                    });
                                }
                            });

                            builder.setNegativeButton("Annuler", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });

                        // listener for the delete theme button
                        deleteButton.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirmation");
                            builder.setMessage("Êtes-vous sûr de vouloir supprimer ce thème ?");
                            builder.setPositiveButton("Oui", (dialog, which) -> {
                                int pos = listView.getPositionForView(v);
                                Topic topic = adapter.getItem(pos);
                                Database.getInstance().removeFrom(Table.TOPICS.getName(), new String[]{"name"}, new String[]{topic.getName()}).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Database.getInstance().removeFrom(Table.TOPIC_USERS.getName(), new String[]{"topic"}, new Topic[]{topic}).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()){
                                                int p = listView.getPositionForView((LinearLayout)v.getParent().getParent());
                                                themes.remove(p);
                                                adapter.notifyDataSetChanged();
                                            } else
                                                Toast.makeText(AdminActivity.this, "Erreur lors de la suppression des utilisateurs du thème", Toast.LENGTH_SHORT).show();
                                        });
                                    } else
                                        Toast.makeText(AdminActivity.this, "Erreur lors de la suppression du thème", Toast.LENGTH_SHORT).show();
                                });
                            });
                            builder.setNegativeButton("Non", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });

                        // listener for change super moderator button
                        changeModeratorButton.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                            builder.setTitle("Changer de Super-Modérateur");
                            builder.setMessage("Entrez le mail du nouveau Super-Modérateur");

                            // Création d'un EditText pour saisir le nom du nouveau Super-Modérateur
                            final EditText input = new EditText(AdminActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            input.setLayoutParams(layoutParams);
                            builder.setView(input);

                            builder.setPositiveButton("Valider", (dialog, which) -> {
                                String newModerator = input.getText().toString().toLowerCase();
                                if (newModerator.isEmpty() || !Email.isValid(newModerator)) {
                                    Toast.makeText(AdminActivity.this, "Un email valide est requis", Toast.LENGTH_SHORT).show();
                                } else {
                                    int pos = listView.getPositionForView(v);
                                    Topic topic = adapter.getItem(pos);
                                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"topic"}, new Topic[]{topic}).addOnSuccessListener(topicUsers2 -> {
                                        User superModo = null;
                                        User futurSuperModo = null;
                                        for (TopicUser tpUsr : topicUsers2) {
                                            if (tpUsr.getRole().getRole() == 4) {
                                                superModo = tpUsr.getUser();
                                            } else if (tpUsr.getUser().getEmail().getAddress().equals(newModerator)) {
                                                futurSuperModo = tpUsr.getUser();
                                            }
                                        }
                                        if (superModo != null && futurSuperModo != null) {
                                            TopicUser newSuperModo = new TopicUser(topic, futurSuperModo, new Role(4), "Super-Modo");
                                            TopicUser oldSuperModo = new TopicUser(topic, superModo, new Role(3), "old Super-Modo");
                                            Database.getInstance().update(Table.TOPIC_USERS.getName(), newSuperModo, new String[]{"topic", "user"}, new Object[]{topic, futurSuperModo});
                                            Database.getInstance().update(Table.TOPIC_USERS.getName(), oldSuperModo, new String[]{"topic", "user"}, new Object[]{topic, superModo});
                                            // Met à jours le texte sur la vue
                                            TextView modoName = ((LinearLayout) v.getParent()).findViewById(R.id.moderatorNameTextView);
                                            modoName.setText(futurSuperModo.getEmail().firstName()+" "+futurSuperModo.getEmail().lastName());
                                        } else {
                                            Toast.makeText(AdminActivity.this, "User not found in topic", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                            builder.setNegativeButton("Annuler", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
                    });

                    return view;
                }
            };

            listView.setAdapter(adapter);
        });
    }
}
