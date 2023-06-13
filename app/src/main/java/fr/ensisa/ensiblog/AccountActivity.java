package fr.ensisa.ensiblog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;
import fr.ensisa.ensiblog.models.Topic;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;


public class AccountActivity extends AppCompatActivity {

    private ArrayList<Topic> themes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        Button buttonEditMdp = findViewById(R.id.buttonEditMdp);
        Button buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
        Button buttonEditBio = findViewById(R.id.buttonEditBio);
        ImageButton imageButtonEditPhoto = findViewById(R.id.imageButtonEditPhoto);
        TextView editTextBio = findViewById(R.id.editTextBio);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewMail = findViewById(R.id.textViewMail);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userUid = user.getUid();


        String userEmail = user.getEmail();
        Email email = new Email(userEmail);
        textViewName.setText(email.firstName() + " " + email.lastName());
        textViewMail.setText(email.getAddress());


        /* Retrieve the user's biography and set it to the editTextBio field
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child(Table.USERS.getName())
                    .child(userUid);
            userRef.child("biographie").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String biography = dataSnapshot.getValue(String.class);
                    editTextBio.setText(biography);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // En cas d'erreur lors de la récupération de la biographie
                    Toast.makeText(AccountActivity.this, "Erreur lors de la récupération de la biographie", Toast.LENGTH_SHORT).show();
                }
            });
        }
*/

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
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userUid);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("biographie", newBio);

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
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Modifiez votre fonction");

                ScrollView scrollView = new ScrollView(AccountActivity.this);
                LinearLayout layout = new LinearLayout(AccountActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                scrollView.addView(layout);

                // Récupérer les TopicUser de l'utilisateur actuel
                Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new Object[]{user})
                        .addOnSuccessListener(topicUsers -> {
                            List<Topic> themes = new ArrayList<>();

                            // Parcourir les TopicUser et récupérer les thèmes correspondants
                            for (TopicUser topicUser : topicUsers) {
                                Topic topic = topicUser.getTopic();
                                themes.add(topic);
                            }

                            // Parcourir la liste des thèmes et ajouter des champs de texte pour chacun
                            for (Topic theme : themes) {
                                TextView textViewTheme = new TextView(AccountActivity.this);
                                textViewTheme.setText(theme.getName());
                                layout.addView(textViewTheme);

                                EditText editTextContent = new EditText(AccountActivity.this);
                                editTextContent.setHint("Modifier votre fonction pour " + theme.getName());
                                layout.addView(editTextContent);
                            }

                            builder.setView(scrollView);

                            builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Récupérer les valeurs saisies par l'utilisateur pour chaque thème
                                    for (int i = 0; i < themes.size(); i++) {
                                        Topic topic = themes.get(i);
                                        EditText editTextContent = (EditText) layout.getChildAt(i * 2 + 1);
                                        String fonction = editTextContent.getText().toString();

                                        if (!fonction.isEmpty()) {
                                            // Rechercher l'utilisateur actuel dans les TopicUser correspondants au thème
                                            for (TopicUser topicUser : topicUsers) {
                                                if (topic.equals(topicUser.getTopic())) {
                                                    // Mettre à jour la fonction de TopicUser
                                                    topicUser.setFonction(fonction);

                                                    // Mettre à jour TopicUser dans la base de données
                                                    Database.getInstance().update(Table.TOPIC_USERS.getName(), topicUser, topicUser);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            });

                            builder.setNegativeButton("Annuler", null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
            }
        });

    }
}