package fr.ensisa.ensiblog;

import static fr.ensisa.ensiblog.Utils.getFilePathFromUri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.UUID;

import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;
import fr.ensisa.ensiblog.models.TopicUser;
import fr.ensisa.ensiblog.models.User;

/**
 * Activity used on the Account_management page of the application
 **/
public class AccountActivity extends AppCompatActivity {

    private final long IMAGE_MAX_SIZE = 1_000_000;

    private final String IMAGE_MAX_SIZE_STRING = "1Mo";

    /**
     * display the bio (called in OnCreate and OnResume)
     **/
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

    /**
     * Function called when AccountActivity starts
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            FirebaseUser user = (FirebaseUser)extras.get("user");
            User currentUser = (User)extras.get("User");


            setContentView(R.layout.activity_account_management);

            Button buttonEditMdp = findViewById(R.id.buttonEditMdp);
            Button buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
            Button buttonEditBio = findViewById(R.id.buttonEditBio);
            Button buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
            TextView textViewName = findViewById(R.id.textViewName);
            TextView textViewMail = findViewById(R.id.textViewMail);
            ImageView imageView2 = findViewById(R.id.imageView2);
            ImageButton imageButtonEditPhoto = findViewById(R.id.imageButtonEditPhoto);

            Email userEmail = currentUser.getEmail();
            textViewName.setText(userEmail.firstName() + " " + userEmail.lastName());
            textViewMail.setText(userEmail.getAddress());

            if(currentUser.getPhotoUrl() != null)
                Picasso.get().load(currentUser.getPhotoUrl()).into(imageView2);

            DisplayBio();

            ActivityResultLauncher<PickVisualMediaRequest> pickImage =
                    registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                        if (uri != null) {
                            ParcelFileDescriptor parcelFileDescriptor = null;
                            try {
                                parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                            } catch (FileNotFoundException e) {
                                Toast.makeText(AccountActivity.this,"Error while reading the size of image",Toast.LENGTH_SHORT).show();
                            }
                            if(parcelFileDescriptor.getStatSize() < IMAGE_MAX_SIZE){
                                Picasso.get().load(uri).into(imageView2);

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
                                ref.putFile(Uri.fromFile(new File(getFilePathFromUri(getContentResolver(),uri)))).continueWithTask(task -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Error while uploading image", Toast.LENGTH_SHORT).show();
                                    } else {
                                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                            currentUser.setPhotoUrl(downloadUrl.toString());
                                            Database.getInstance().update(Table.USERS.getName(), currentUser, new String[]{"uid"}, new String[]{currentUser.getUid()});
                                            Toast.makeText(AccountActivity.this, "Photo de profil mise à jour", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    return null;
                                });

                            } else {
                                Toast.makeText(AccountActivity.this,"Error, image is too big must be < "+IMAGE_MAX_SIZE_STRING,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // listener for editMdp button
            buttonEditMdp.setOnClickListener(v -> {
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

                builder.setPositiveButton("Valider", (dialog, which) -> {
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
                        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), currentPassword);
                        user.reauthenticate(credential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Current Password is verified :
                                        user.updatePassword(newPassword)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toast.makeText(AccountActivity.this, "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(AccountActivity.this, "Échec de la modification du mot de passe", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Mot de passe actuel incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            });


            buttonEditBio.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Saisir sa biographie");

                // Créer un EditText pour la saisie du texte
                final EditText editTextBio = new EditText(AccountActivity.this);
                builder.setView(editTextBio);

                builder.setPositiveButton("Enregistrer", (dialogInterface, i) -> {
                    String newBio = editTextBio.getText().toString().trim();

                    // Récupérer l'utilisateur actuellement connecté
                    FirebaseUser currentUser12 = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser12 != null) {
                        String userUid = currentUser12.getUid();


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
                                                TextView editTextBio1 = findViewById(R.id.editTextBio);
                                                editTextBio1.setText(newBio);
                                                Toast.makeText(AccountActivity.this, "Biographie mise à jour", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(AccountActivity.this, "Erreur de connexion, reconnectez vous", Toast.LENGTH_SHORT).show();
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
            });


            buttonEditFunctions.setOnClickListener(view -> {

                // Récupérer la liste des thèmes auxquels l'utilisateur est abonné
                Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{"user"}, new User[]{currentUser}).addOnSuccessListener(topicUsers -> {
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
            });



            buttonDeleteAccount.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Supprimer le compte");
                builder.setMessage("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

                builder.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Supprimer l'utilisateur de la base de données Firebase
                        FirebaseUser currentUser1 = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser1 != null) {
                            String userUid = currentUser1.getUid();
                            Database.getInstance().removeFrom(Table.USERS.getName(), new String[]{"uid"}, new String[]{userUid}).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    currentUser1.delete();
                                    Database.getInstance().get(Table.TOPIC_USERS.getName(), TopicUser.class, new String[]{}, new String[]{})
                                            .addOnSuccessListener(topicUserList -> {
                                                for (TopicUser topicUser : topicUserList) {
                                                    User user1 = topicUser.getUser();
                                                    if (user1 != null) {
                                                        String userUidFromTopicUser = user1.getUid();
                                                        if (userUidFromTopicUser != null && userUidFromTopicUser.equals(userUid)) {
                                                            Database.getInstance().removeFrom(Table.TOPIC_USERS.getName(), new String[]{"user"}, new User[]{user1}).addOnCompleteListener(task2 -> {
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
            });


            imageButtonEditPhoto.setOnClickListener(v -> {
                pickImage.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            });
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        DisplayBio();
    }
}