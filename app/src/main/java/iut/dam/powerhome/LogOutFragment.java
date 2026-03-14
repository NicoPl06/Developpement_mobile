package iut.dam.powerhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;

public class LogOutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle b) {
        View view = inflater.inflate(R.layout.fragment_log_out, c, false);

        Button buttonLogOut = view.findViewById(R.id.btn_logout);



        buttonLogOut.setOnClickListener(v -> {
            Snackbar snackbar = Snackbar.make(view, "Voulez-vous vraiment vous déconnecter ?", Snackbar.LENGTH_LONG);

            snackbar.setAction("OUI", view1 -> {

                procederDeconnexion();
            });
            snackbar.show();
        });

        return view;
    }
    private void procederDeconnexion() {
        Toast.makeText(getContext(), "Déconnexion réussie !", Toast.LENGTH_SHORT).show();

        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar()!=null){
            activity.getSupportActionBar().setTitle("Log out");
        }
    }
}