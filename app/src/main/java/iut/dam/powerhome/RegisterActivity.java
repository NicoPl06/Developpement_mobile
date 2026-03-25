package iut.dam.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String BASE_URL        = "http://10.0.2.2/server/";
    private static final String URL_REGISTER    = BASE_URL + "register.php";
    private static final String URL_GET_HABITATS= BASE_URL + "getHabitats.php";
    private static final String URL_ASSIGN      = BASE_URL + "assignHabitat.php";

    private EditText etFirstname, etLastname, etEmail, etPassword, etPhone;
    private Spinner  spinnerIndicatif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        etFirstname      = findViewById(R.id.et_firstname);
        etLastname       = findViewById(R.id.et_lastname);
        etEmail          = findViewById(R.id.et_email_register);
        etPassword       = findViewById(R.id.et_password_register);
        etPhone          = findViewById(R.id.editTextPhone);
        spinnerIndicatif = findViewById(R.id.spinnerIndicatif);
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String firstname = etFirstname.getText().toString().trim();
        String lastname  = etLastname.getText().toString().trim();
        String email     = etEmail.getText().toString().trim();
        String password  = etPassword.getText().toString().trim();
        String indicatif = spinnerIndicatif.getSelectedItem().toString();
        String phone     = indicatif + etPhone.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URL_REGISTER,
                response -> {
                    android.util.Log.d("RAW_RESPONSE", response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optString("status").equals("success")) {
                            int userId = json.optInt("id", -1);
                            String token = json.optString("token", "");

                            getSharedPreferences("SESSIONS", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_token", token)
                                    .putInt("user_id", userId)
                                    .apply();

                            fetchHabitatsAndShowDialog(userId);

                        } else {
                            String err = json.optString("error", "Erreur inconnue");
                            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Réponse serveur invalide", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau ou serveur inaccessible", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("firstname", firstname);
                p.put("lastname",  lastname);
                p.put("email",     email);
                p.put("password",  password);
                p.put("phone",     phone);
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void fetchHabitatsAndShowDialog(int userId) {
        StringRequest request = new StringRequest(Request.Method.GET, URL_GET_HABITATS,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        List<Habitat> habitats = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id       = obj.optInt("id");
                            double area  = obj.optDouble("area", 0);
                            int floor    = obj.optInt("floor", 0);
                            String fname = obj.optString("firstname", "");
                            String lname = obj.optString("lastname", "");
                            String name  = (fname + " " + lname).trim();
                            if (name.isEmpty()) name = "Habitat #" + id;
                            habitats.add(new Habitat(id, name, floor, area, new ArrayList<>()));
                        }

                        showHabitatDialog(userId, habitats);

                    } catch (JSONException e) {

                        showHabitatDialog(userId, new ArrayList<>());
                    }
                },
                error -> showHabitatDialog(userId, new ArrayList<>())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showHabitatDialog(int userId, List<Habitat> habitats) {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_habitat, null);

        Button  btnCreate     = dialogView.findViewById(R.id.btn_create_habitat);
        Button  btnJoin       = dialogView.findViewById(R.id.btn_join_habitat);
        View    panelCreate   = dialogView.findViewById(R.id.panel_create);
        EditText etArea       = dialogView.findViewById(R.id.et_habitat_area);
        EditText etFloor      = dialogView.findViewById(R.id.et_habitat_floor);
        Button  btnConfirmCreate = dialogView.findViewById(R.id.btn_confirm_create);
        View    panelJoin     = dialogView.findViewById(R.id.panel_join);
        ListView listHabitats = dialogView.findViewById(R.id.list_habitats);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        btnCreate.setOnClickListener(v -> {
            panelCreate.setVisibility(View.VISIBLE);
            panelJoin.setVisibility(View.GONE);
            btnCreate.setAlpha(1f);
            btnJoin.setAlpha(0.5f);
        });

        btnConfirmCreate.setOnClickListener(v -> {
            String areaStr  = etArea.getText().toString().trim();
            String floorStr = etFloor.getText().toString().trim();
            if (areaStr.isEmpty() || floorStr.isEmpty()) {
                Toast.makeText(this, "Veuillez renseigner surface et étage", Toast.LENGTH_SHORT).show();
                return;
            }
            double area  = Double.parseDouble(areaStr);
            int    floor = Integer.parseInt(floorStr);
            assignHabitat(userId, "create", 0, area, floor, dialog);
        });

        btnJoin.setOnClickListener(v -> {
            panelJoin.setVisibility(View.VISIBLE);
            panelCreate.setVisibility(View.GONE);
            btnJoin.setAlpha(1f);
            btnCreate.setAlpha(0.5f);
        });

        if (!habitats.isEmpty()) {
            String[] labels = new String[habitats.size()];
            for (int i = 0; i < habitats.size(); i++) {
                Habitat h = habitats.get(i);
                labels[i] = h.ResidentName + "  |  " + h.area + " m²  |  Étage " + h.floor;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_single_choice, labels);
            listHabitats.setAdapter(adapter);
            listHabitats.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            listHabitats.setOnItemClickListener((parent, view, position, id) -> {
                int selectedHabitatId = habitats.get(position).HabitatID;
                assignHabitat(userId, "join", selectedHabitatId, 0, 0, dialog);
            });
        } else {

            btnJoin.setEnabled(false);
            btnJoin.setAlpha(0.3f);
            TextView tvNoHabitat = dialogView.findViewById(R.id.tv_no_habitat);
            if (tvNoHabitat != null) tvNoHabitat.setVisibility(View.VISIBLE);
        }

        panelCreate.setVisibility(View.VISIBLE);
        panelJoin.setVisibility(View.GONE);

        dialog.show();
    }

    private void assignHabitat(int userId, String action, int habitatId,
                               double area, int floor, AlertDialog dialog) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_ASSIGN,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optString("status").equals("success")) {
                            dialog.dismiss();
                            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, HabitatActivity_Frag.class));
                            finish();
                        } else {
                            Toast.makeText(this, json.optString("error", "Erreur"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Réponse serveur invalide", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id",    String.valueOf(userId));
                p.put("action",     action);
                p.put("habitat_id", String.valueOf(habitatId));
                p.put("area",       String.valueOf(area));
                p.put("floor",      String.valueOf(floor));
                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}