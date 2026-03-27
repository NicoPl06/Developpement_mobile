package iut.dam.powerhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HabitatFragment extends Fragment {

    private ListView listHabitats;
    private HabitatAdapter adapter;
    private List<Habitat> items = new ArrayList<>();
    private static final String URL_API = "http://10.0.2.2/server/getHabitats.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habitats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listHabitats = view.findViewById(R.id.listHabitats);
        adapter = new HabitatAdapter(requireActivity(), R.layout.item_habitat, items);
        listHabitats.setAdapter(adapter);
        loadDataFromDatabase();
        listHabitats.setOnItemClickListener((parent, v, position, id) -> showDetails(items.get(position)));

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Habitats");
        }
    }

    private void loadDataFromDatabase() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_API,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        items.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.optInt("id", 0);

                            String proName = obj.optString("display_name", "");
                            if (proName.isEmpty()) {
                                String fName = obj.optString("firstname", "");
                                String lName = obj.optString("lastname", "");
                                proName = (fName + " " + lName).trim();
                            }

                            // Co-résidents
                            List<String> coNames = new ArrayList<>();
                            if (!obj.isNull("co_names")) {
                                JSONArray coArr = obj.getJSONArray("co_names");
                                for (int j = 0; j < coArr.length(); j++) {
                                    coNames.add(coArr.getString(j));
                                }
                            }
                            int floor = obj.optInt("floor", 0);
                            double area = obj.optDouble("area", 0.0);

                            List<Appliance> appliances = new ArrayList<>();
                            if (!obj.isNull("appliances")) {
                                JSONArray appArray = obj.getJSONArray("appliances");
                                for (int j = 0; j < appArray.length(); j++) {
                                    JSONObject appObj = appArray.getJSONObject(j);
                                    String rawName = appObj.optString("Name", "Appareil");
                                    if (rawName.equals("Appareil")) rawName = appObj.optString("name", "Appareil");
                                    String displayName = rawName.contains(" (") ? rawName.split(" \\(")[0] : rawName;
                                    appliances.add(new Appliance(
                                            appObj.optInt("id", 0),
                                            displayName,
                                            appObj.optString("reference", ""),
                                            appObj.optInt("wattage", 0),
                                            ApplianceType.valueOfName(rawName)
                                    ));
                                }
                            }
                            items.add(new Habitat(id, proName, coNames, floor, area, appliances));

                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erreur serveur", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    private void showDetails(Habitat h) {
        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        b.setTitle(h.ResidentName);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_habitat, null);
        TextView txtSurface   = dialogView.findViewById(R.id.txtSurface);
        LinearLayout container = dialogView.findViewById(R.id.container);

        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        int accentColor = ColorManager.getColor(requireContext(), userId);

        txtSurface.setText("Surface : " + h.area + " m²");
        txtSurface.setTextColor(accentColor);

        for (Appliance a : h.appliances) {
            View item = getLayoutInflater().inflate(R.layout.item_appliance, container, false);
            ImageView icon  = item.findViewById(R.id.icon);
            TextView txtName = item.findViewById(R.id.txtName);
            TextView txtWatt = item.findViewById(R.id.txtWatt);

            txtName.setText(a.Name);
            txtWatt.setText(a.wattage + " W");

            if (a.wattage < 100)       txtWatt.setTextColor(getResources().getColor(R.color.jaune));
            else if (a.wattage < 150)  txtWatt.setTextColor(getResources().getColor(R.color.orange));
            else                       txtWatt.setTextColor(getResources().getColor(R.color.rouge));

            if (a.type != null) {
                switch (a.type) {
                    case WASHING_MACHINE: icon.setImageResource(R.drawable.ic_washing_machine); break;
                    case VACUUM:          icon.setImageResource(R.drawable.ic_vacuum);          break;
                    case CLIM:            icon.setImageResource(R.drawable.ic_clim);            break;
                    case IRON:            icon.setImageResource(R.drawable.ic_iron);            break;
                }
            }

            // Couleur dynamique sur les icônes
            icon.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);

            container.addView(item);
        }

        b.setView(dialogView);
        b.show();
    }
}