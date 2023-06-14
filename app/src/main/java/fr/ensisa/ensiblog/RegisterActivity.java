package fr.ensisa.ensiblog;

import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.firebase.Database;
import fr.ensisa.ensiblog.firebase.Table;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;
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

            if(!Email.isValid(email)) {
                Toast.makeText(getApplicationContext(), "Invalid uha email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Password.isValid(password)) {
                Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                return;
            }

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
                                Log.i("n6a","GO to login !");
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