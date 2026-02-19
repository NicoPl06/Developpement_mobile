//package iut.dam.powerhome;
//
//import android.os.Bundle;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HabitatActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.habitat_activity);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.habitat), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        //ListView habitants = findViewById(R.id.listHabitats);
//
//
//        List<Habitat> items = new ArrayList<>();
//
//        List<Appliance> g1 = new ArrayList<>();
//        g1.add(new Appliance(1, "Machine à laver", "ML456", 200, ApplianceType.WASHING_MACHINE));
//        g1.add(new Appliance(2, "Aspirateur", "A500", 50, ApplianceType.VACUUM));
//        g1.add(new Appliance(3, "Climatisation", "CL200", 120, ApplianceType.CLIM));
//        g1.add(new Appliance(4, "Fer à repasser", "FR100", 90, ApplianceType.IRON));
//        items.add(new Habitat(1, "Gaëtan Leclair", 1, 45.0, g1));
//
//        List<Appliance> g2 = new ArrayList<>();
//        g2.add(new Appliance(5, "Machine à laver", "ML789", 180, ApplianceType.WASHING_MACHINE));
//        items.add(new Habitat(2, "Cédric Boudet", 1, 38.0, g2));
//
//        List<Appliance> g3 = new ArrayList<>();
//        g3.add(new Appliance(6, "Climatisation", "CL300", 110, ApplianceType.CLIM));
//        g3.add(new Appliance(7, "Aspirateur", "A700", 60, ApplianceType.VACUUM));
//        items.add(new Habitat(3, "Gaylord Thibodeaux", 2, 52.0, g3));
//
//        List<Appliance> g4 = new ArrayList<>();
//        g4.add(new Appliance(8, "Machine à laver", "ML287", 190, ApplianceType.WASHING_MACHINE));
//        g4.add(new Appliance(9, "Fer à repasser", "FR453", 85, ApplianceType.IRON));
//        g4.add(new Appliance(10, "Aspirateur", "A200", 60, ApplianceType.VACUUM ));
//        items.add(new Habitat(4, "Adam Jacquinot", 3, 48.0,g4 ));
//
//        List<Appliance> g5 = new ArrayList<>();
//        g5.add(new Appliance(11, "Aspirateur", "A120", 55, ApplianceType.VACUUM));
//        items.add(new Habitat(5, "Abel Fresnel", 3, 54.0, g5));
//
//
//        HabitatAdapter adapter = new HabitatAdapter(
//                this,
//                R.layout.item_habitat,
//                items
//        );
//
//        habitants.setAdapter(adapter);
//
//        habitants.setOnItemClickListener((parent, view, position, id) -> {
//            Habitat h = items.get(position);
//            Toast.makeText(this, h.ResidentName, Toast.LENGTH_SHORT).show();
//        });
//    }
//}
