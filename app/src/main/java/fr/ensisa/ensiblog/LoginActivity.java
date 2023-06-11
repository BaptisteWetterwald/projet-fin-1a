package fr.ensisa.ensiblog;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;

import static fr.ensisa.ensiblog.Utils.showInfoBox;

public class LoginActivity extends AppCompatActivity {
    EditText edUsername, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edUsername = findViewById(R.id.editTextLoginUsername);
        edPassword = findViewById(R.id.editTextLoginPassword);
        Button buttonLog = (Button) findViewById(R.id.buttonLogin);
        Button buttonReg = (Button) findViewById(R.id.buttonRegister);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FirebaseUser user = (FirebaseUser) extras.get("user");
            edUsername.setText(user.getEmail());
        }

        buttonLog.setOnClickListener(v -> {

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

            auth.signInUser(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if(auth.getCurrentUser().isEmailVerified()){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("user",auth.getCurrentUser());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && auth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("user",auth.getCurrentUser());
                                startActivity(intent);
                            } else {
                                showInfoBox("Alert !","Please verify your email","OK",LoginActivity.this, (dialog, which) -> {
                                    dialog.cancel();
                                    auth.getCurrentUser().sendEmailVerification();
                                });
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Wrong mail/password",Toast.LENGTH_SHORT).show();
                }
            });
        });
        buttonReg.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
    }
}