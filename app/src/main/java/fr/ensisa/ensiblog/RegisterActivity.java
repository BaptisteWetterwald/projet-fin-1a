package fr.ensisa.ensiblog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Text;
import fr.ensisa.ensiblog.models.User;

import static fr.ensisa.ensiblog.Utils.showInfoBox;

public class RegisterActivity extends AppCompatActivity {
    EditText edUsername, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edUsername = findViewById(R.id.editTextLoginUsername);
        edPassword = findViewById(R.id.editTextLoginPassword);
        Button buttonReg = (Button) findViewById(R.id.buttonRegister);
        Button buttonLog = (Button) findViewById(R.id.buttonLogin);

        buttonReg.setOnClickListener(v -> {
            String email = edUsername.getText().toString();
            String password = edPassword.getText().toString();

            Authentication auth = new Authentication();

            auth.createUser(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();
                    Database.getInstance().add(Table.USERS.getName(), new User(new Email(email),uid), User.class).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            showInfoBox("Alert !","Please verify your email","OK",RegisterActivity.this ,(dialog, which) -> {
                                dialog.cancel();
                                user.sendEmailVerification();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "An error occurred while creating your account", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred while creating your account", Toast.LENGTH_SHORT).show();
                }
            });
        });
        buttonLog.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
        });
    }
}