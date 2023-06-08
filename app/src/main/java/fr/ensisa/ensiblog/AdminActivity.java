package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private ListView listView;
    private TextView moderatorNameTextView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listView);

        // Exemple de données des thèmes du blog
        String[] themes = {"Thème 1", "Thème 2", "Thème 3", "Thème 4", "Thème 5"};

        // Adapter personnalisé pour la liste des thèmes
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.itemTextView, themes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Button editButton = view.findViewById(R.id.editButton);
                // Ajoutez ici la logique pour gérer le clic sur le bouton "Modifier le nom"
                return view;
            }
        };

        // Associer l'adaptateur à la ListView
        listView.setAdapter(adapter);


    }
}
