package iut.dam.powerhome;

import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import java.lang.reflect.Array;

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
        String[] items = {"Gaëtan Leclair", "Cédric Boudet", "Gaylord Thibodeaux", "Adam Jacquinot", "Abel Fresnel", "Marc Lémery", "Alceste Rodin", "Henry Vernier", "Manuel Mossé"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        items);
        habitants.setAdapter(adapter);

        habitants.setOnItemClickListener(((parent, view, position, id) -> {
            String nom = items[position];
            Toast.makeText(this, nom, Toast.LENGTH_SHORT ).show();
        }));

        /**
         super.onCreate(savedInstanceState);
         setContentView(R.layout.habitat_activity);

         TextView tvEmail = findViewById(R.id.Email);

         String email = getIntent().getStringExtra("email");
         String password = getIntent().getStringExtra("mdp");

         tvEmail.setText("Hello '" + email + "' ,your password is " + password);
         */
    }
}
