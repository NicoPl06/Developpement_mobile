package iut.dam.powerhome;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HabtitatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.habitat_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.habitat), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView habitants = findViewById(R.id.listHabitats);

        // Création de la liste d'habitats
        List<Habitat> items = new ArrayList<>();
        items.add(new Habitat(1, "Gaëtan Leclair", 1, 45.0,
                new Appliance(1, "Frigo", "FR123", 200)));
        items.add(new Habitat(2, "Cédric Boudet", 1, 38.0,
                new Appliance(2, "TV", "TV456", 120)));
        items.add(new Habitat(3, "Gaylord Thibodeaux", 2, 52.0,
                new Appliance(3, "PC", "PC789", 300)));

        HabitatAdapter adapter = new HabitatAdapter(
                this,
                R.layout.item_habitat,
                items
        );

        habitants.setAdapter(adapter);

        habitants.setOnItemClickListener((parent, view, position, id) -> {
            Habitat h = items.get(position);
            Toast.makeText(this, h.ResidentName, Toast.LENGTH_SHORT).show();
        });
    }
}


/**
 super.onCreate(savedInstanceState);
 setContentView(R.layout.habitat_activity);

 TextView tvEmail = findViewById(R.id.Email);

 String email = getIntent().getStringExtra("email");
 String password = getIntent().getStringExtra("mdp");

 tvEmail.setText("Hello '" + email + "' ,your password is " + password);
 */

