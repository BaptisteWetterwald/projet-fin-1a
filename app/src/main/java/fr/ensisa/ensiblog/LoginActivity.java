package fr.ensisa.ensiblog;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import java.util.Locale;
import static fr.ensisa.ensiblog.Utils.showInfoBox;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import fr.ensisa.ensiblog.firebase.Authentication;
import fr.ensisa.ensiblog.models.Email;
import fr.ensisa.ensiblog.models.Password;

/**
 * Activity used on the login page of the application
 **/
public class LoginActivity extends AppCompatActivity {
    EditText edUsername, edPassword;
    Button btnFr, btnEng;

    /**
     * Function called when LoginActivity starts
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Authentication authentication = new Authentication();
        FirebaseUser firebaseUser = authentication.getCurrentUser();

        if(firebaseUser != null && firebaseUser.isEmailVerified()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("user",firebaseUser);
            startActivity(intent);
        } else {
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

           /* btnFr = (Button) findViewById(R.id.francais);
            btnFr.setOnClickListener(v -> {
                setAppLocale("fr");                               IN PROCESS !!!!!!!!!!!!!!!
            });
            btnEng = (Button) findViewById(R.id.anglais);
            btnEng.setOnClickListener(v -> {
                setAppLocale("eng");
            });*/

            buttonLog.setOnClickListener(v -> {

                String email = edUsername.getText().toString().toLowerCase();
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
                            Log.i("n6a","EMail already verified !");
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra("user",auth.getCurrentUser());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()){
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

/*    private void setAppLocale(String localeCode){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }*/

}