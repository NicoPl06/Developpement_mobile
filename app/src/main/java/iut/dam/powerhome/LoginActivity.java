package iut.dam.powerhome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login(View v) {
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String url = "http://10.0.2.2/server/login.php";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optString("status").equals("success")) {
                            int userId = jsonResponse.optInt("id", -1);
                            String token = jsonResponse.optString("token", "");

                            getSharedPreferences("SESSIONS", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_token", token)
                                    .putInt("user_id", userId)
                                    .apply();

                            Toast.makeText(getApplicationContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HabitatActivity_Frag.class));
                            finish();
                        } else {
                            String errorMsg = jsonResponse.optString("error", "Identifiants incorrects");
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Erreur de réponse du serveur", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    android.util.Log.e("VOLLEY", error.toString());
                    Toast.makeText(getApplicationContext(), "Erreur réseau ou serveur inaccessible", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}