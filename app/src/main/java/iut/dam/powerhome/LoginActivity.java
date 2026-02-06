package iut.dam.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    EditText email, mdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);


        email = findViewById(R.id.Email);
        mdp = findViewById(R.id.MDP);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void login(View v){
        Log.i("LoginActivity:login","bouton cliqué !! ");

        /** String Email = email.getText().toString().trim();
        String Password = mdp.getText().toString().trim();

        if((Email.equals("abcd")) && (Password.equals("EFGH"))){

            Intent intent = new Intent(LoginActivity.this, HabtitatActivity.class);
            intent.putExtra("email",Email);
            intent.putExtra("mdp",Password);
            startActivity(intent);
        } else {
            Toast t = Toast.makeText(v.getContext(), "Identifiants incorrects", Toast.LENGTH_SHORT);
            t.show();
        }

        */


         Snackbar s = Snackbar.make(v,"ATTENTION",Snackbar.LENGTH_SHORT);

        s.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(v.getContext(),"ANNULER",Toast.LENGTH_SHORT);
                t.show();
            }
        });
        s.show();


    }


}