package iut.dam.powerhome;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.*;

import android.app.AlertDialog;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HabitatFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habitats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView habitants = view.findViewById(R.id.listHabitats);
        List<Habitat> items = new ArrayList<>();

        // ---- Tes données exactes depuis HabitatActivity ----
        List<Appliance> g1 = new ArrayList<>();
        g1.add(new Appliance(1, "Machine à laver", "ML456", 200, ApplianceType.WASHING_MACHINE));
        g1.add(new Appliance(2, "Aspirateur", "A500", 50, ApplianceType.VACUUM));
        g1.add(new Appliance(3, "Climatisation", "CL200", 120, ApplianceType.CLIM));
        g1.add(new Appliance(4, "Fer à repasser", "FR100", 90, ApplianceType.IRON));
        items.add(new Habitat(1, "Gaëtan Leclair", 1, 45.0, g1));

        List<Appliance> g2 = new ArrayList<>();
        g2.add(new Appliance(5, "Machine à laver", "ML789", 180, ApplianceType.WASHING_MACHINE));
        items.add(new Habitat(2, "Cédric Boudet", 1, 38.0, g2));

        List<Appliance> g3 = new ArrayList<>();
        g3.add(new Appliance(6, "Climatisation", "CL300", 110, ApplianceType.CLIM));
        g3.add(new Appliance(7, "Aspirateur", "A700", 60, ApplianceType.VACUUM));
        items.add(new Habitat(3, "Gaylord Thibodeaux", 2, 52.0, g3));

        List<Appliance> g4 = new ArrayList<>();
        g4.add(new Appliance(8, "Machine à laver", "ML287", 190, ApplianceType.WASHING_MACHINE));
        g4.add(new Appliance(9, "Fer à repasser", "FR453", 85, ApplianceType.IRON));
        g4.add(new Appliance(10, "Aspirateur", "A200", 60, ApplianceType.VACUUM));
        items.add(new Habitat(4, "Adam Jacquinot", 3, 48.0, g4));

        List<Appliance> g5 = new ArrayList<>();
        g5.add(new Appliance(11, "Aspirateur", "A120", 55, ApplianceType.VACUUM));
        items.add(new Habitat(5, "Abel Fresnel", 3, 54.0, g5));

        // ---- Adapter (ton HabitatAdapter existant, item_habitat.xml) ----
        HabitatAdapter adapter = new HabitatAdapter(
                requireActivity(),
                R.layout.item_habitat,
                items
        );
        habitants.setAdapter(adapter);

        habitants.setOnItemClickListener((parent, v, position, id) -> {
            Habitat h = items.get(position);

            AlertDialog.Builder b = new AlertDialog.Builder(requireContext()); // ← this devient requireContext()
            b.setTitle(h.ResidentName);
            LayoutInflater inflater = requireActivity().getLayoutInflater(); // ← getLayoutInflater() devient ça
            View dialogView = inflater.inflate(R.layout.dialog_habitat, null);
            TextView txtSurface = dialogView.findViewById(R.id.txtSurface);
            LinearLayout container = dialogView.findViewById(R.id.container);
            txtSurface.setText("Surface : " + h.area + " m²");

            for (Appliance a : h.appliances) {
                View item = inflater.inflate(R.layout.item_appliance, container, false);
                ImageView icon = item.findViewById(R.id.icon);
                TextView txtName = item.findViewById(R.id.txtName);
                TextView txtWatt = item.findViewById(R.id.txtWatt);
                txtName.setText(a.Name);
                txtWatt.setText(a.wattage + " W");

                if (a.wattage < 100) {
                    txtWatt.setTextColor(Color.parseColor("#FBC02D"));
                } else if (a.wattage < 150) {
                    txtWatt.setTextColor(Color.parseColor("#FB8C00"));
                } else {
                    txtWatt.setTextColor(Color.parseColor("#D32F2F"));
                }

                switch (a.type) {
                    case WASHING_MACHINE: icon.setImageResource(R.drawable.ic_washing_machine); break;
                    case VACUUM:          icon.setImageResource(R.drawable.ic_vacuum);          break;
                    case CLIM:            icon.setImageResource(R.drawable.ic_clim);            break;
                    case IRON:            icon.setImageResource(R.drawable.ic_iron);            break;
                }
                container.addView(item);
            }

            b.setView(dialogView);
            b.show();
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Habitats");
        }
    }
}
