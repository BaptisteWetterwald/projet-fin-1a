package fr.ensisa.ensiblog;

import android.content.Intent;
import android.os.Bundle;
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
import android.content.DialogInterface;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;

public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listView);
        Button newThemeButton = findViewById(R.id.newThemeButton);

        // listener for New Theme button
        newThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String themeName = themeNameEditText.getText().toString();
                        String moderatorEmail = moderatorNameEditText.getText().toString();
                        int selectedRole = readerRadioButton.isChecked() ? 1 : 2;
                        if (themeName.isEmpty()) {
                            Toast.makeText(AdminActivity.this, "Un nom de thème est requis", Toast.LENGTH_SHORT).show();
                        } else if (moderatorEmail.isEmpty() && Email.isValid(moderatorEmail)) {
                            Toast.makeText(AdminActivity.this, "Un mail correct est requis", Toast.LENGTH_SHORT).show();
                        } else {
                            Topic newTopic = new Topic(themeName,new Role(selectedRole));
                            Database.getInstance().add(Table.TOPICS.getName(), newTopic, Topic.class,new String[]{"name"});
                            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"email"}, new Email[]{new Email(moderatorEmail)}).addOnSuccessListener(users -> {
                                if(users.size()>0)
                                    Database.getInstance().add(Table.TOPIC_USERS.getName(), new TopicUser(newTopic,users.get(0),new Role(selectedRole)), TopicUser.class,true);
                                else {
                                    Utils.showInfoBox("Alert !","User not found with email","OK",AdminActivity.this, (dialog2, which2) -> {
                                        dialog2.cancel();
                                    });
                                }
                            });
                        }
                    }
                });

                //.addOnSuccessListener(o -> Toast.makeText(AdminActivity.this,"Topic successfully created !",Toast.LENGTH_SHORT))

                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Database.getInstance().get(Table.TOPICS.getName(), Topic.class,new String[]{},new Object[]{}).addOnSuccessListener(topics -> {

            String[] themes = new String[topics.size()];
            for (int i = 0; i < topics.size(); i++) {
                themes[i] = topics.get(i).getName();
            }
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.itemTextView, themes) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    Button editButton = view.findViewById(R.id.editButton);
                    Button deleteButton = view.findViewById(R.id.deleteButton);
                    Button changeModeratorButton = view.findViewById(R.id.changeModeratorButton);

                    // listener for the Theme Name Edit button
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                            builder.setTitle("Changer nom du thème");
                            builder.setMessage("Entrez le nouveau nom du thème");

                            final EditText input = new EditText(AdminActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            input.setLayoutParams(layoutParams);
                            builder.setView(input);

                            builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newThemeName = input.getText().toString();
                                    if (newThemeName.isEmpty()) {
                                        Toast.makeText(AdminActivity.this, "Un nom de thème est requis", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Call the function to change theme name

                                        // Returns the name of the item (theme) we're in (for database calling)
                                        // int position = listView.getPositionForView(v);
                                        // String itemName = adapter.getItem(position);
                                    }
                                }
                            });

                            builder.setNegativeButton("Annuler", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                    // listener for the delete theme button
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirmation");
                            builder.setMessage("Êtes-vous sûr de vouloir supprimer ce thème ?");
                            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Call delete theme function here

                                    // Returns the name of the item we're in (for database calling)
                                    // int position = listView.getPositionForView(v);
                                    // String itemName = adapter.getItem(position);
                                }
                            });
                            builder.setNegativeButton("Non", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                    // listener for change super moderator button
                    changeModeratorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                            builder.setTitle("Changer de Super-Modérateur");
                            builder.setMessage("Entrez le nom du nouveau Super-Modérateur");

                            // Création d'un EditText pour saisir le nom du nouveau Super-Modérateur
                            final EditText input = new EditText(AdminActivity.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                            input.setLayoutParams(layoutParams);
                            builder.setView(input);

                            builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newModerator = input.getText().toString();
                                    if (newModerator.isEmpty()) {
                                        Toast.makeText(AdminActivity.this, "Un nom est requis", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Call the function to change supermodo

                                        // Returns the name of the item we're in (for database calling)
                                        // int position = listView.getPositionForView(v);
                                        // String itemName = adapter.getItem(position);
                                    }
                                }
                            });

                            builder.setNegativeButton("Annuler", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });


                    return view;
                }
            };

        // Associate adapter to the ListView
            listView.setAdapter(adapter);
        });
    }
}
