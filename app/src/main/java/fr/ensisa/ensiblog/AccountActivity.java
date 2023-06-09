package fr.ensisa.ensiblog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import fr.ensisa.ensiblog.models.Password;


public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        Button buttonEditMdp = findViewById(R.id.buttonEditMdp);
        Button buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
        Button buttonEditBio = findViewById(R.id.buttonEditBio);
        ImageButton imageButtonEditPhoto = findViewById(R.id.imageButtonEditPhoto);
        TextView editTextBio = findViewById(R.id.editTextBio);

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
                            // add function to modify account's password
                            // Il faut d'abord vérifer que currentPassword est le bon MDP du compte
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
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                builder.setTitle("Modifier sa description");

                final EditText input = new EditText(AccountActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String description = input.getText().toString();
                        editTextBio.setText(description);

                        // add the Description to the database (or modify it)
                    }
                });

                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



    }
}