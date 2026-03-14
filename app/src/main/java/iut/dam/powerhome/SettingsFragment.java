package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private float currentScale;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle b) {
        View v = inflater.inflate(R.layout.fragment_settings, c, false);

        prefs = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        currentScale = prefs.getFloat("fontScale", getResources().getConfiguration().fontScale);

        TextView tvPourcentage = v.findViewById(R.id.tv_pourcentage);
        Button btnPlus = v.findViewById(R.id.btnPlus);
        Button btnMoins = v.findViewById(R.id.btnMoins);
        Button btnTheme = v.findViewById(R.id.btnTheme);

        updateUI(tvPourcentage);

        btnPlus.setOnClickListener(view -> {
            if (currentScale < 1.6f) {
                currentScale += 0.2f;
                applyAndSaveScale();
            }
        });

        btnMoins.setOnClickListener(view -> {
            if (currentScale > 0.8f) {
                currentScale -= 0.2f;
                applyAndSaveScale();
            }
        });

        btnTheme.setOnClickListener(view -> {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        return v;
    }

    private void updateUI(TextView tv) {
        int percent = Math.round(currentScale * 100);
        tv.setText(percent + "%");
    }

    private void applyAndSaveScale() {
        prefs.edit().putFloat("fontScale", currentScale).apply();

        Configuration config = getResources().getConfiguration();
        config.fontScale = currentScale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        if (getActivity() != null) {
            getActivity().recreate();
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar()!=null){
            activity.getSupportActionBar().setTitle("Settings");
        }
    }
}