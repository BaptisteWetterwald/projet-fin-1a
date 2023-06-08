package fr.ensisa.ensiblog;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private TextView textViewName;
    private TextView textViewMail;
    private Button buttonEditPhoto;
    private ImageButton imageButtonEditPhoto;
    private Button buttonEditMdp;
    private Button buttonEditFunctions;
    private TextView textViewBio;
    private EditText editTextBio;
    private Button buttonResiterBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        textViewName = findViewById(R.id.textViewName);
        textViewMail = findViewById(R.id.textViewMail);
        //buttonEditPhoto = findViewById(R.id.buttonEditPhoto);
        imageButtonEditPhoto = findViewById(R.id.imageButtonEditPhoto);
        buttonEditMdp = findViewById(R.id.buttonEditMdp);
        buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
        textViewBio = findViewById(R.id.textViewBio);
        editTextBio = findViewById(R.id.editTextBio);
        buttonResiterBio = findViewById(R.id.buttonRegisterBio);

        buttonEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* affiche pop-up pour choisir une nouvelle photo dans le téléphone */
            }
        });

        imageButtonEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* affiche pop-up pour choisir une nouvelle photo dans le téléphone */
            }
        });

        buttonEditMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* affiche pop-up pour changer le Mdp, (editText Mdp, editText Nouveau Mdp,
                editText Vérification Nouveau Mdp, button confirmer */
            }
        });

        buttonEditMdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* affiche grosse pop-up qui affiche en liste scrolable verticalement ou dans chaque
                * ligne il y a en text */
            }
        });

        buttonResiterBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*e ditTextBio.getText(); variable à mettre dans la base de données */
            }
        });




    }
}
