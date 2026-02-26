package iut.dam.powerhome;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private float currentBrightness = 0.5f;
    private final String[] languageCodes = {"en", "fr", "es"};
    private final float[] fontScales = {0.85f, 0.95f, 1.0f, 1.15f, 1.30f};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle b) {
        return inflater.inflate(R.layout.fragment_settings, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button nightBtn = view.findViewById(R.id.night);
        Button BtnP    = view.findViewById(R.id.BtnPlus);
        Button BtnM    = view.findViewById(R.id.BtnMoins);
        Spinner spinner = view.findViewById(R.id.spinnerLanguage);
        SeekBar seekBar = view.findViewById(R.id.seekBarDisplaySize);
        TextView tvValue = view.findViewById(R.id.tvDisplaySizeValue);

        // ---- Bouton couleur ----
        View colorSwatch = view.findViewById(R.id.colorSwatch);
        Button btnColor  = view.findViewById(R.id.btnPickColor);

        // Afficher la couleur courante dans le swatch
        colorSwatch.setBackgroundColor(ColorManager.getColor(requireContext()));

        btnColor.setOnClickListener(v -> {
            ColorPickerDialog.newInstance(color -> {
                // 1. Sauvegarder
                ColorManager.saveColor(requireContext(), color);
                // 2. Mettre à jour le swatch
                colorSwatch.setBackgroundColor(color);
                // 3. Appliquer immédiatement à l'activité (toolbar, nav header, etc.)
                if (getActivity() instanceof HabitatActivity_Frag) {
                    ((HabitatActivity_Frag) getActivity()).applyAccentColor(color);
                }
            }).show(getParentFragmentManager(), "colorPicker");
        });

        // ---- Night mode ----
        nightBtn.setOnClickListener(v -> {
            int current = AppCompatDelegate.getDefaultNightMode();
            AppCompatDelegate.setDefaultNightMode(
                    current == AppCompatDelegate.MODE_NIGHT_YES
                            ? AppCompatDelegate.MODE_NIGHT_NO
                            : AppCompatDelegate.MODE_NIGHT_YES
            );
            requireActivity().recreate();
        });

        // ---- Luminosité ----
        BtnP.setOnClickListener(v -> {
            currentBrightness += 0.1f;
            setScreenBrightness(currentBrightness);
        });
        BtnM.setOnClickListener(v -> {
            currentBrightness -= 0.1f;
            setScreenBrightness(currentBrightness);
        });

        // ---- Spinner langue ----
        String[] languageNames = {"English", "Français", "Español"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, languageNames
        );
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
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                String sel = languageCodes[pos];
                if (!sel.equals(Locale.getDefault().getLanguage())) applyLocale(sel);
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        // ---- SeekBar taille ----
        float activeFontScale = requireContext().getResources().getConfiguration().fontScale;
        int initialProgress = findClosestIndex(activeFontScale);
        seekBar.setProgress(initialProgress);
        tvValue.setText(getScaleLabel(initialProgress));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean u) {
                tvValue.setText(getScaleLabel(p));
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {
                float chosen = fontScales[s.getProgress()];
                if (chosen != activeFontScale) applyFontScale(chosen);
            }
        });

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Settings");
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

    private String getScaleLabel(int progress) {
        switch (progress) {
            case 0: return getString(R.string.display_size_small);
            case 1: return getString(R.string.display_size_normal) + " -";
            case 2: return getString(R.string.display_size_normal);
            case 3: return getString(R.string.display_size_large);
            case 4: return getString(R.string.display_size_xlarge);
            default: return getString(R.string.display_size_normal);
        }
    }

    private int findClosestIndex(float scale) {
        int best = 2;
        float minDiff = Float.MAX_VALUE;
        for (int i = 0; i < fontScales.length; i++) {
            float diff = Math.abs(fontScales[i] - scale);
            if (diff < minDiff) { minDiff = diff; best = i; }
        }
        return best;
    }

    private void applyFontScale(float scale) {
        Configuration config = new Configuration(requireActivity().getResources().getConfiguration());
        config.fontScale = scale;
        requireActivity().getBaseContext().getResources()
                .updateConfiguration(config, requireActivity().getResources().getDisplayMetrics());
        requireActivity().recreate();
    }
}