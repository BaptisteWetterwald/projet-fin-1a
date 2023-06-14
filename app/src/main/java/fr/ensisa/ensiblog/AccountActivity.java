package fr.ensisa.ensiblog;

import android.content.Intent;
import android.util.Log;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Role;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;



import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.ensisa.ensiblog.models.Password;


public class AccountActivity extends AppCompatActivity {

    private ArrayList<Topic> themes = new ArrayList<>();
    private User userModel;

    // Méthode pour afficher la bio (appelée dans OnCreate et OnResume)
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
                                TextView editTextBio = findViewById(R.id.editTextBio);
                                editTextBio.setText(currentBio);
                            }
                        }
                    }
                });
    }


    // Method to get a list of the user's subbed themes
    private List<TopicUser> GetTopicUsers(String userUid) {
        List<TopicUser> topicUsers = new ArrayList<>();
        Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new Object[]{userUid}).addOnSuccessListener(userList -> {
            User ourUser = userList.get(0);
            Log.e("DEBUG", "ourUser" + ourUser);
            Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{ourUser})
                    .addOnSuccessListener(topicUserList -> {
                        Log.e("DEBUG", "taille de topicUserList :" + topicUserList.size());
                        Log.e("DEBUG", "topicUserList :" + topicUserList);
                        // Creates a list of themes
                        topicUsers.addAll(topicUserList);
                        Log.e("DEBUG", "taille de topicUsers :" + topicUsers.size());
                        Log.e("DEBUG", "topicUsers :" + topicUsers);
                    });
        });
        Log.e("DEBUG", "return de topicUsers :" + topicUsers);
        return topicUsers;
    }

    private List<String> GetTopicNames(List<TopicUser> TopicUsers) {
        List<String> topicNames = new ArrayList<>();
        for (TopicUser topicUser : TopicUsers) {
            topicNames.add(topicUser.getTopic().getName());
        }
        return topicNames;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        Button buttonEditMdp = findViewById(R.id.buttonEditMdp);
        Button buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
        Button buttonEditBio = findViewById(R.id.buttonEditBio);
        Button buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        ImageButton imageButtonEditPhoto = findViewById(R.id.imageButtonEditPhoto);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewMail = findViewById(R.id.textViewMail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userUid = user.getUid();


        String userEmail = user.getEmail();
        Email email = new Email(userEmail);
        textViewName.setText(email.firstName() + " " + email.lastName());
        textViewMail.setText(email.getAddress());

        DisplayBio();

        // listener for editMdp button
        buttonEditMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Changer de mot de passe");

                LinearLayout layout = new LinearLayout(AccountActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                EditText currentPasswordText = new EditText(AccountActivity.this);
                currentPasswordText.setHint("Mot de passe actuel");
                currentPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(currentPasswordText);

                EditText newPasswordText = new EditText(AccountActivity.this);
                newPasswordText.setHint("Nouveau mot de passe");
                newPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(newPasswordText);

                EditText confirmNewPasswordText = new EditText(AccountActivity.this);
                confirmNewPasswordText.setHint("Confirmer nouveau mot de passe");
                confirmNewPasswordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                layout.addView(confirmNewPasswordText);

                builder.setView(layout);

                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String currentPassword = currentPasswordText.getText().toString();
                        String newPassword = newPasswordText.getText().toString();
                        String confirmNewPassword = confirmNewPasswordText.getText().toString();

                        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                            Toast.makeText(AccountActivity.this, "Un mot de passe est requis", Toast.LENGTH_SHORT).show();
                        } else if (!newPassword.equals(confirmNewPassword)) {
                            Toast.makeText(AccountActivity.this, "Les mots de passe ne sont pas identiques", Toast.LENGTH_SHORT).show();
                        } else if (!Password.isValid(newPassword)) {
                            Toast.makeText(AccountActivity.this, "Le mot de passe n'est pas assez sécurisé", Toast.LENGTH_SHORT).show();
                        } else {
                            // Check if current password is correct
                            assert user != null;
                            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Current Password is verified :
                                                user.updatePassword(newPassword)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(AccountActivity.this, "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(AccountActivity.this, "Échec de la modification du mot de passe", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(AccountActivity.this, "Mot de passe actuel incorrect", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        buttonEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Saisir sa biographie");

                // Créer un EditText pour la saisie du texte
                final EditText editTextBio = new EditText(AccountActivity.this);
                builder.setView(editTextBio);

                builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newBio = editTextBio.getText().toString().trim();

                        // Récupérer l'utilisateur actuellement connecté
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userUid = currentUser.getUid();


                            // Récupérer la liste des utilisateurs existants
                            Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{}, new String[]{})
                                    .addOnSuccessListener(userList -> {
                                        // Parcourir la liste des utilisateurs pour trouver l'utilisateur actuel
                                        for (User user1 : userList) {
                                            if (user1.getUid() != null) {
                                                if (user1.getUid().equals(userUid)) {
                                                    // Dupliquer l'utilisateur
                                                    User updatedUser = new User();
                                                    updatedUser.setEmail(user1.getEmail());
                                                    updatedUser.setBiographie(newBio);
                                                    updatedUser.setUid(user1.getUid());

                                                    Database.getInstance().update(Table.USERS.getName(), updatedUser, new String[]{"uid"}, new String[]{userUid});
                                                    TextView editTextBio = findViewById(R.id.editTextBio);
                                                    editTextBio.setText(newBio);
                                                    Toast.makeText(AccountActivity.this, "Biographie mise à jour", Toast.LENGTH_SHORT).show();
                                                    break;
                                                }
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(AccountActivity.this, "Erreur de connexion, reconnectez vous", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        buttonEditFunctions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupérer l'utilisateur actuellement connecté
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Database.getInstance().get(Table.USERS.getName(), User.class, new String[]{"uid"}, new String[]{user.getUid()}).addOnSuccessListener(users -> {
                        if (users.size() > 0) {
                            userModel = users.get(0);
                            // Récupérer la liste des thèmes auxquels l'utilisateur est abonné
                            Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{userModel}).addOnSuccessListener(topicUsers -> {
                                // Créer le dialogue avec la liste des thèmes et les zones de texte
                                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                                builder.setTitle("Modifier sa fonction");

                                LinearLayout layout = new LinearLayout(AccountActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);

                                // Ajouter les thèmes et les zones de texte à la disposition linéaire
                                for (int t = 0; t < topicUsers.size(); t++) {
                                    TextView textView = new TextView(AccountActivity.this);
                                    String topicName = topicUsers.get(t).getTopic().getName();
                                    textView.setText(topicName);
                                    layout.addView(textView);

                                    EditText editText = new EditText(AccountActivity.this);
                                    layout.addView(editText);
                                }

                                builder.setView(layout);

                                // Ajouter le bouton "Enregistrer"
                                builder.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Parcourir les zones de texte et mettre à jour les objets TopicUser
                                        for (int j = 0; j < topicUsers.size(); j++) {
                                            EditText editText = (EditText) layout.getChildAt(j * 2 + 1);
                                            String fonction = editText.getText().toString();
                                            Log.e("DEBUG", "fonction " + fonction);
                                            if (fonction.length() > 0) {
                                                // Mettre à jour la fonction pour l'objet TopicUser correspondant
                                                TopicUser topicUser = topicUsers.get(j);
                                                Log.e("DEBUG", "TopicUser " + topicUser.getTopic().getName());
                                                TopicUser newTopicUser = new TopicUser(topicUser.getTopic(), topicUser.getUser(), topicUser.getRole(), fonction);

                                                // Enregistrer la modification dans la base de données
                                                Database.getInstance().update(Table.TOPIC_USERS.getName(), newTopicUser, new String[]{"topic", "user"}, new Object[]{topicUser.getTopic(),topicUser.getUser()});
                                            }
                                        }

                                        // Afficher un message de succès
                                        Toast.makeText(AccountActivity.this, "Fonctions enregistrées", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                // Ajouter le bouton "Annuler"
                                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            });
                        }
                    });
                }
            }
        });







        buttonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Supprimer le compte");
                builder.setMessage("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

                builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Supprimer l'utilisateur de la base de données Firebase
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userUid = currentUser.getUid();
                            Database.getInstance().removeFrom(Table.USERS.getName(), new String[]{"uid"}, new String[]{userUid}).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    currentUser.delete();
                                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{}, new String[]{})
                                            .addOnSuccessListener(topicUserList -> {
                                                for (TopicUser topicUser : topicUserList) {
                                                    User user = topicUser.getUser();
                                                    if (user != null) {
                                                        String userUidFromTopicUser = user.getUid();
                                                        if (userUidFromTopicUser != null && userUidFromTopicUser.equals(userUid)) {
                                                            Database.getInstance().removeFrom(Table.TOPIC_USERS.getName(), new String[]{"user"}, new User[]{user}).addOnCompleteListener(task2 -> {
                                                                if (task2.isSuccessful()) {
                                                                    Toast.makeText(AccountActivity.this, "Votre compte à bien été supprimé", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(AccountActivity.this, "Echec de la suppression du compte", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        DisplayBio();
    }
}