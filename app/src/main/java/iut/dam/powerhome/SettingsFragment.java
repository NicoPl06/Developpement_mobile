package iut.dam.powerhome;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment  {

    private float currentBrightness = 0.5f;

    private final String[] languageCodes = {"en", "fr", "es"};

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

        Spinner spinner = view.findViewById(R.id.spinnerLanguage);

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


        // --- Spinner langue ---
        // Noms affichés toujours en natif pour que l'utilisateur reconnaisse sa langue
        String[] languageNames = {"English", "Français", "Español"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                languageNames
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Pré-sélectionner la langue actuellement active
        String currentLang = Locale.getDefault().getLanguage(); // "en", "fr", "es"
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                spinner.setSelection(i, false); // false = sans déclencher onItemSelected
                break;
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selectedCode = languageCodes[position];
                String currentCode  = Locale.getDefault().getLanguage();

                // On ne relance l'activité que si la langue change vraiment
                if (!selectedCode.equals(currentCode)) {
                    applyLocale(selectedCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

    private void applyLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(requireContext().getResources().getConfiguration());
        config.setLocale(locale);

        // Applique la config à l'activité
        requireActivity()
                .getBaseContext()
                .getResources()
                .updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());

        // Recrée l'activité pour que tous les textes soient rechargés
        requireActivity().recreate();
    }

}