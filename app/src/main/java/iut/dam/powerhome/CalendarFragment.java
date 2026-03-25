package iut.dam.powerhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/server/";

    private TextView tvEcocoins;
    private RecyclerView rvSlots;
    private ViewGroup llDaySelector;
    private View progressBar;

    private final List<TimeSlot> allSlots = new ArrayList<>();
    private final List<TimeSlot> filteredSlots = new ArrayList<>();
    private SlotAdapter slotAdapter;

    private List<Appliance> myAppliances = new ArrayList<>();
    private int userId;
    private String selectedDay = null; // "2026-06-20"

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEcocoins   = view.findViewById(R.id.tv_ecocoins);
        rvSlots      = view.findViewById(R.id.rv_slots);
        llDaySelector = view.findViewById(R.id.ll_day_selector);
        progressBar  = view.findViewById(R.id.progressbar_calendar);

        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        rvSlots.setLayoutManager(new LinearLayoutManager(getContext()));
        slotAdapter = new SlotAdapter(filteredSlots, this::onBookClick);
        rvSlots.setAdapter(slotAdapter);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Calendrier");
        }

        loadEcocoins();
        loadCalendar();
        loadMyAppliances();
    }

    // ─────────── Chargement éco-coins ───────────

    private void loadEcocoins() {
        String url = BASE_URL + "getMyBookings.php?user_id=" + userId;
        Volley.newRequestQueue(requireContext()).add(new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        int coins = json.optInt("ecocoins", 0);
                        tvEcocoins.setText(coins + " éco-coins");
                        tvEcocoins.setTextColor(coins >= 0 ? Color.parseColor("#A5D6A7") : Color.parseColor("#EF9A9A"));
                    } catch (JSONException ignored) {}
                }, error -> {}));
    }

    // ─────────── Chargement calendrier ───────────

    private void loadCalendar() {
        progressBar.setVisibility(View.VISIBLE);
        String url = BASE_URL + "getCalendar.php";
        Volley.newRequestQueue(requireContext()).add(new StringRequest(url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray slots = json.getJSONArray("slots");
                        allSlots.clear();

                        for (int i = 0; i < slots.length(); i++) {
                            JSONObject s = slots.getJSONObject(i);
                            allSlots.add(new TimeSlot(
                                    s.getInt("id"),
                                    s.getString("begin_time"),
                                    s.getString("end_time"),
                                    s.getInt("percent"),
                                    s.getString("color"),
                                    s.getInt("maxWattage"),
                                    s.getInt("bookedWattage")
                            ));
                        }

                        buildDaySelector();

                        // Sélectionner le premier jour par défaut
                        if (!allSlots.isEmpty()) {
                            selectedDay = allSlots.get(0).begin_time.substring(0, 10);
                            filterByDay(selectedDay);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Erreur chargement calendrier", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
        }));
    }

    // ─────────── Sélecteur de jours ───────────

    private void buildDaySelector() {
        if (getContext() == null || !isAdded()) return;
        llDaySelector.removeAllViews();

        // Collecter les jours uniques dans l'ordre
        List<String> days = new ArrayList<>();
        for (TimeSlot s : allSlots) {
            String day = s.begin_time.substring(0, 10);
            if (!days.contains(day)) days.add(day);
        }

        SimpleDateFormat inputFmt  = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        SimpleDateFormat labelFmt  = new SimpleDateFormat("EEE\ndd/MM", Locale.FRANCE);

        int accentColor = ColorManager.getColor(requireContext());

        for (String day : days) {
            String label;
            try {
                Date d = inputFmt.parse(day);
                label = labelFmt.format(d);
            } catch (Exception e) {
                label = day;
            }

            Button btn = new Button(requireContext());
            btn.setText(label);
            btn.setTextSize(11f);
            btn.setAllCaps(false);

            int w = dpToPx(72);
            int h = dpToPx(60);
            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(w, h);
            lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            btn.setLayoutParams(lp);

            final String finalDay = day;
            btn.setOnClickListener(v -> {
                selectedDay = finalDay;
                filterByDay(finalDay);
                // Mettre à jour les couleurs des boutons
                for (int i = 0; i < llDaySelector.getChildCount(); i++) {
                    View child = llDaySelector.getChildAt(i);
                    if (child instanceof Button) {
                        ((Button) child).setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        child == v ? accentColor : Color.parseColor("#E0E0E0")));
                        ((Button) child).setTextColor(child == v ? Color.WHITE : Color.DKGRAY);
                    }
                }
            });

            // Style initial
            if (day.equals(selectedDay)) {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accentColor));
                btn.setTextColor(Color.WHITE);
            } else {
                btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                btn.setTextColor(Color.DKGRAY);
            }

            llDaySelector.addView(btn);
        }
    }

    private void filterByDay(String day) {
        filteredSlots.clear();
        for (TimeSlot s : allSlots) {
            if (s.begin_time.startsWith(day)) filteredSlots.add(s);
        }
        slotAdapter.notifyDataSetChanged();
    }

    // ─────────── Chargement appareils de l'utilisateur ───────────

    private void loadMyAppliances() {
        String url = BASE_URL + "getUserHomeData.php?id=" + userId;
        Volley.newRequestQueue(requireContext()).add(new StringRequest(url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        myAppliances.clear();
                        if (!json.isNull("appliances")) {
                            JSONArray apps = json.getJSONArray("appliances");
                            for (int i = 0; i < apps.length(); i++) {
                                JSONObject o = apps.getJSONObject(i);
                                String rawName = o.optString("name", "Appareil");
                                String displayName = rawName.contains(" (") ? rawName.split(" \\(")[0] : rawName;
                                myAppliances.add(new Appliance(
                                        o.getInt("id"),
                                        displayName,
                                        o.optString("reference", ""),
                                        o.optInt("wattage", 0),
                                        ApplianceType.valueOfName(rawName)
                                ));
                            }
                        }
                    } catch (JSONException ignored) {}
                }, error -> {}));
    }

    // ─────────── Clic sur "Réserver" ───────────

    private void onBookClick(TimeSlot slot) {
        if (myAppliances.isEmpty()) {
            Toast.makeText(getContext(), "Vous n'avez aucun appareil dans votre habitat.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_book_slot, null);
        TextView tvTitle   = dialogView.findViewById(R.id.tv_dialog_slot_title);
        TextView tvInfo    = dialogView.findViewById(R.id.tv_dialog_slot_info);
        TextView tvLoad    = dialogView.findViewById(R.id.tv_dialog_load);
        TextView tvHint    = dialogView.findViewById(R.id.tv_dialog_ecocoin_hint);
        Spinner spinner    = dialogView.findViewById(R.id.spinner_appliance_book);

        // Formater l'heure
        String begin = slot.begin_time.length() >= 16 ? slot.begin_time.substring(11, 16) : slot.begin_time;
        String end   = slot.end_time.length()   >= 16 ? slot.end_time.substring(11, 16)   : slot.end_time;
        String dateLabel = slot.begin_time.length() >= 10 ? slot.begin_time.substring(0, 10) : "";

        tvTitle.setText("Réserver · " + begin + " – " + end);
        tvInfo.setText("Créneau : " + dateLabel + " de " + begin + " à " + end);

        // Info charge + bonus/malus
        String loadLabel;
        int ecocoinsDelta;
        if (slot.percent <= 30) {
            loadLabel = "Charge : " + slot.percent + " %  ✅ Bonus +10 éco-coins";
            tvLoad.setTextColor(Color.parseColor("#388E3C"));
            tvHint.setText("+10 🌿");
            tvHint.setTextColor(Color.parseColor("#A5D6A7"));
            ecocoinsDelta = 10;
        } else if (slot.percent <= 70) {
            loadLabel = "Charge : " + slot.percent + " %  ➡ Neutre (0 éco-coins)";
            tvLoad.setTextColor(Color.parseColor("#E65100"));
            tvHint.setText("±0 🌿");
            tvHint.setTextColor(Color.parseColor("#FFCC80"));
            ecocoinsDelta = 0;
        } else {
            loadLabel = "Charge : " + slot.percent + " %  ⚠ Malus -10 éco-coins";
            tvLoad.setTextColor(Color.parseColor("#C62828"));
            tvHint.setText("-10 🌿");
            tvHint.setTextColor(Color.parseColor("#EF9A9A"));
            ecocoinsDelta = -10;
        }
        tvLoad.setText(loadLabel);

        // Spinner avec les appareils
        List<String> names = new ArrayList<>();
        for (Appliance a : myAppliances) names.add(a.Name + " – " + a.wattage + " W");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    int selectedIdx = spinner.getSelectedItemPosition();
                    if (selectedIdx < 0 || selectedIdx >= myAppliances.size()) return;
                    Appliance chosen = myAppliances.get(selectedIdx);
                    bookSlot(slot, chosen, ecocoinsDelta);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // ─────────── Appel API réservation ───────────

    private void bookSlot(TimeSlot slot, Appliance appliance, int expectedDelta) {
        String url = BASE_URL + "bookSlot.php";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if ("success".equals(json.optString("status"))) {
                            int delta   = json.optInt("ecocoins_delta", 0);
                            int balance = json.optInt("new_balance", 0);

                            String msg;
                            if (delta > 0)      msg = "✅ Réservé ! +" + delta + " éco-coins (solde : " + balance + ")";
                            else if (delta < 0) msg = "⚠ Réservé avec malus de " + Math.abs(delta) + " éco-coins (solde : " + balance + ")";
                            else                msg = "✅ Réservé ! Créneau neutre (solde : " + balance + ")";

                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                            tvEcocoins.setText(balance + " éco-coins");
                            tvEcocoins.setTextColor(balance >= 0 ? Color.parseColor("#A5D6A7") : Color.parseColor("#EF9A9A"));
                            // Recharger le calendrier pour mettre à jour la charge
                            loadCalendar();
                        } else {
                            Toast.makeText(getContext(), json.optString("error", "Erreur"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Réponse invalide", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id",      String.valueOf(userId));
                p.put("appliance_id", String.valueOf(appliance.ID));
                p.put("timeslot_id",  String.valueOf(slot.id));
                return p;
            }
        };
        Volley.newRequestQueue(requireContext()).add(req);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    // Import manquant pour LinearLayoutCompat
    // (remplacer par LinearLayout.LayoutParams dans le vrai projet si non utilisé)
    static class LinearLayoutCompat {
        static class LayoutParams extends ViewGroup.MarginLayoutParams {
            LayoutParams(int w, int h) { super(w, h); }
        }
    }
}