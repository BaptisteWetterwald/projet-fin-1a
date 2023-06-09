package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonEditMdp;
    private Button buttonEditFunctions;
    private Button buttonEditBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        buttonEditMdp = findViewById(R.id.buttonEditMdp);
        buttonEditFunctions = findViewById(R.id.buttonEditFunctions);
        buttonEditBio = findViewById(R.id.buttonEditBio);

        buttonEditMdp.setOnClickListener(this);
        buttonEditFunctions.setOnClickListener(this);
        buttonEditBio.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Gérer les clics sur les boutons
        switch (view.getId()) {
            case R.id.buttonEditMdp:
                // Action à effectuer lors du clic sur le bouton "MOT DE PASSE"
                break;
            case R.id.buttonEditFunctions:
                // Action à effectuer lors du clic sur le bouton "Fonction par thème"
                break;
            case R.id.buttonEditBio:
                // Action à effectuer lors du clic sur le bouton "Modifier sa description"
                break;
        }
    }
}
