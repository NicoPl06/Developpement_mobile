package iut.dam.powerhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvHabitatArea, tvHabitatFloor, tvNoAppliances;
    private RecyclerView rvAppliances;
    private ApplianceAdapter adapter;
    private List<Appliance> applianceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.my_home);
        }

        rvAppliances.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApplianceAdapter(applianceList);
        rvAppliances.setAdapter(adapter);
        loadUserData();

        ImageButton btnAjoutApp = view.findViewById(R.id.btn_add_appliance);

        btnAjoutApp.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_appliance, null);

            Spinner spinner = dialogView.findViewById(R.id.spinner_appliance_type);
            EditText etName = dialogView.findViewById(R.id.et_appliance_name);
            EditText etWatt = dialogView.findViewById(R.id.et_appliance_wattage);
            EditText etRef = dialogView.findViewById(R.id.et_appliance_ref);

            ApplianceType[] types = ApplianceType.values();
            String[] typeNames = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                typeNames[i] = types[i].name();
            }

            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeNames);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpinner);

            new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .setPositiveButton("Ajouter", (dialog, which) -> {
                        String selectedType = spinner.getSelectedItem().toString();
                        String typedName = etName.getText().toString().trim();
                        String finalName = typedName.isEmpty() ? selectedType : typedName + " (" + selectedType + ")";
                        String watt = etWatt.getText().toString().trim();
                        String ref = etRef.getText().toString().trim();

                        if (!watt.isEmpty()) {
                            sendApplianceToServer(finalName, Integer.parseInt(watt), ref);
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void initViews(View v) {
        tvUserName = v.findViewById(R.id.tv_user_name);
        tvUserEmail = v.findViewById(R.id.tv_user_email);
        tvHabitatArea = v.findViewById(R.id.tv_habitat_area);
        tvHabitatFloor = v.findViewById(R.id.tv_habitat_floor);
        rvAppliances = v.findViewById(R.id.rv_appliances);
        tvNoAppliances = v.findViewById(R.id.tv_no_appliances);
    }

    private void loadUserData() {
        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) return;

        String url = "http://10.0.2.2/server/getUserHomeData.php?id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    if (!isAdded() || getContext() == null) return;

                    try {
                        JSONObject json = new JSONObject(response);
                        tvUserName.setText(getString(R.string.name) + " " + json.getString("firstname") + " " + json.getString("lastname"));
                        tvUserEmail.setText(getString(R.string.Email_2points) + " " + json.getString("email"));
                        tvHabitatArea.setText(getString(R.string.area) + " " + json.optDouble("area", 0) + " m²");
                        tvHabitatFloor.setText(getString(R.string.floor) + " " + json.optInt("floor", 0));

                        applianceList.clear();
                        if (!json.isNull("appliances")) {
                            JSONArray apps = json.getJSONArray("appliances");

                            for (int i = 0; i < apps.length(); i++) {
                                JSONObject obj = apps.getJSONObject(i);

                                String rawName = obj.optString("name", "Appliance");

                                String displayName = rawName.contains(" (") ? rawName.split(" \\(")[0] : rawName;

                                applianceList.add(new Appliance(
                                        obj.getInt("id"),
                                        displayName,
                                        obj.optString("reference", ""),
                                        obj.optInt("wattage", 0),
                                        ApplianceType.valueOfName(rawName)
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        if (applianceList.isEmpty()) {
                            tvNoAppliances.setVisibility(View.VISIBLE);
                            rvAppliances.setVisibility(View.GONE);
                        } else {
                            tvNoAppliances.setVisibility(View.GONE);
                            rvAppliances.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show());
        queue.add(stringRequest);
    }

    private void sendApplianceToServer(String name, int wattage, String reference) {
        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        String url = "http://10.0.2.2/server/addAppliances.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "Appareil ajouté avec succès !", Toast.LENGTH_SHORT).show();
                    applianceList.clear();
                    loadUserData();
                },
                error -> Toast.makeText(getContext(), "Erreur réseau : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("name", name);
                params.put("wattage", String.valueOf(wattage));
                params.put("reference", reference);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(postRequest);
    }
}