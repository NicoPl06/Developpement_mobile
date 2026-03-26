package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
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

public class SettingsFragment extends Fragment {

    private float currentBrightness = 0.5f;
    private final String[] languageCodes = {"en", "fr", "es"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle b) {
        return inflater.inflate(R.layout.fragment_settings, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button nightBtn  = view.findViewById(R.id.night);
        Button BtnP      = view.findViewById(R.id.BtnPlus);
        Button BtnM      = view.findViewById(R.id.BtnMoins);
        Spinner spinner  = view.findViewById(R.id.spinnerLanguage);
        View colorSwatch = view.findViewById(R.id.colorSwatch);
        Button btnColor  = view.findViewById(R.id.btnPickColor);

        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        colorSwatch.setBackgroundColor(ColorManager.getColor(requireContext(), userId));

        btnColor.setOnClickListener(v -> {
            ColorPickerDialog.newInstance(userId, color -> {
                ColorManager.saveColor(requireContext(), color, userId);
                colorSwatch.setBackgroundColor(color);
                if (getActivity() instanceof HabitatActivity_Frag) {
                    ((HabitatActivity_Frag) getActivity()).applyAccentColor(color);
                }
            }).show(getParentFragmentManager(), "colorPicker");
        });

        nightBtn.setOnClickListener(v -> {
            int current = AppCompatDelegate.getDefaultNightMode();
            AppCompatDelegate.setDefaultNightMode(
                    current == AppCompatDelegate.MODE_NIGHT_YES
                            ? AppCompatDelegate.MODE_NIGHT_NO
                            : AppCompatDelegate.MODE_NIGHT_YES
            );
            requireActivity().recreate();
        });

        BtnP.setOnClickListener(v -> {
            currentBrightness += 0.1f;
            setScreenBrightness(currentBrightness);
        });
        BtnM.setOnClickListener(v -> {
            currentBrightness -= 0.1f;
            setScreenBrightness(currentBrightness);
        });

        String[] languageNames = {"English", "Français", "Español"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String currentLang = Locale.getDefault().getLanguage();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                spinner.setSelection(i, false);
                break;
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                String sel = languageCodes[pos];
                if (!sel.equals(Locale.getDefault().getLanguage())) applyLocale(sel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getString(R.string.settings));
        }
    }

    private void setScreenBrightness(float b) {
        if (b < 0f) b = 0f;
        if (b > 1f) b = 1f;
        android.view.WindowManager.LayoutParams p = requireActivity().getWindow().getAttributes();
        p.screenBrightness = b;
        requireActivity().getWindow().setAttributes(p);
    }

    private void applyLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(requireContext().getResources().getConfiguration());
        config.setLocale(locale);
        requireActivity().getBaseContext().getResources()
                .updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());
        requireActivity().recreate();
    }
}