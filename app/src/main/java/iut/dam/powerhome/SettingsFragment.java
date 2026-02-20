package iut.dam.powerhome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment  {

    private float currentBrightness = 0.5f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle b) {
        return inflater.inflate(R.layout.fragment_settings, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button nightBtn = view.findViewById(R.id.night);

        Button BtnP = view.findViewById(R.id.BtnPlus);
        Button BtnM = view.findViewById(R.id.BtnMoins);

        nightBtn.setOnClickListener(v -> {
            int current = AppCompatDelegate.getDefaultNightMode();

            if (current == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            requireActivity().recreate();
        });

        BtnP.setOnClickListener(v ->{
            currentBrightness += 0.1f;
            setScreenBrightness(currentBrightness);


        });

        BtnM.setOnClickListener(v -> {
            currentBrightness -= 0.1f;
            setScreenBrightness(currentBrightness);
        });


        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Settings");
        }

    }

    private void setScreenBrightness(float brightness) {
        if (brightness < 0f) brightness = 0f;
        if (brightness > 1f) brightness = 1f;

        android.view.WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.screenBrightness = brightness;
        getActivity().getWindow().setAttributes(params);
    }

}