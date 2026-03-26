package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MyBookingsFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/server/";
    private TextView tvEcocoinsBalance;
    private RecyclerView rvMyBookings;
    private View llEmpty;
    private BookingAdapter bookingAdapter;
    private final List<Booking> bookings = new ArrayList<>();
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEcocoinsBalance = view.findViewById(R.id.tv_ecocoins_balance);
        rvMyBookings      = view.findViewById(R.id.rv_my_bookings);
        llEmpty           = view.findViewById(R.id.ll_empty_bookings);

        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        rvMyBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingAdapter = new BookingAdapter(bookings, this::onCancelClicked);
        rvMyBookings.setAdapter(bookingAdapter);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Mes réservations");
        }

        loadBookings();
    }

    private void loadBookings() {
        String url = BASE_URL + "getMyBookings.php?user_id=" + userId;
        Volley.newRequestQueue(requireContext()).add(new StringRequest(url,
                response -> {
                    if (!isAdded() || getContext() == null) return;
                    try {
                        JSONObject json = new JSONObject(response);
                        updateEcocoinsDisplay(json.optInt("ecocoins", 0));

                        bookings.clear();
                        JSONArray arr = json.getJSONArray("bookings");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject b = arr.getJSONObject(i);
                            bookings.add(new Booking(
                                    b.optInt("appliance_id", 0),
                                    b.optInt("timeslot_id", 0),
                                    b.optString("appliance_name", "Appareil"),
                                    b.optString("begin_time", ""),
                                    b.optString("end_time", ""),
                                    b.optInt("wattage", 0),
                                    b.optInt("ecocoins_delta", 0),
                                    b.optInt("cancellable", 0) == 1,
                                    ApplianceType.valueOfName(b.optString("appliance_name", ""))
                            ));
                        }

                        if (bookings.isEmpty()) {
                            rvMyBookings.setVisibility(View.GONE);
                            llEmpty.setVisibility(View.VISIBLE);
                        } else {
                            rvMyBookings.setVisibility(View.VISIBLE);
                            llEmpty.setVisibility(View.GONE);
                            bookingAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}));
    }

    private void onCancelClicked(Booking booking) {
        String deltaMsg = booking.ecocoinsDelta > 0
                ? "Vous perdrez " + booking.ecocoinsDelta + " éco-coin(s)."
                : "Vous récupérerez " + Math.abs(booking.ecocoinsDelta) + " éco-coin(s).";

        new AlertDialog.Builder(requireContext())
                .setTitle("Annuler la réservation")
                .setMessage("Annuler « " + booking.applianceName + " » ?\n\n" + deltaMsg)
                .setPositiveButton("Confirmer", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("Retour", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        String url = BASE_URL + "cancelBooking.php";
        Volley.newRequestQueue(requireContext()).add(new StringRequest(
                Request.Method.POST, url,
                response -> {
                    if (!isAdded() || getContext() == null) return;
                    try {
                        JSONObject json = new JSONObject(response);
                        if ("success".equals(json.optString("status"))) {
                            updateEcocoinsDisplay(json.optInt("new_balance", 0));
                            bookings.remove(booking);
                            bookingAdapter.notifyDataSetChanged();
                            if (bookings.isEmpty()) {
                                rvMyBookings.setVisibility(View.GONE);
                                llEmpty.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(getContext(), "Réservation annulée ✓", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                },
                error -> Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("appliance_id", String.valueOf(booking.applianceId));
                params.put("timeslot_id", String.valueOf(booking.timeslotId));
                return params;
            }
        });
    }

    private void updateEcocoinsDisplay(int coins) {
        tvEcocoinsBalance.setText(String.valueOf(coins));
        tvEcocoinsBalance.setTextColor(coins >= 0 ? Color.WHITE : Color.parseColor("#EF9A9A"));
    }

    public static class Booking {
        public int applianceId, timeslotId, wattage, ecocoinsDelta;
        public String applianceName, beginTime, endTime;
        public boolean cancellable;
        public ApplianceType type;

        public Booking(int applianceId, int timeslotId, String applianceName, String beginTime,
                       String endTime, int wattage, int ecocoinsDelta, boolean cancellable, ApplianceType type) {
            this.applianceId = applianceId;
            this.timeslotId = timeslotId;
            this.applianceName = applianceName;
            this.beginTime = beginTime;
            this.endTime = endTime;
            this.wattage = wattage;
            this.ecocoinsDelta = ecocoinsDelta;
            this.cancellable = cancellable;
            this.type = type;
        }
    }

    interface OnCancelListener { void onCancel(Booking booking); }

    public static class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BVH> {
        private final List<Booking> list;
        private final OnCancelListener cancelListener;

        BookingAdapter(List<Booking> list, OnCancelListener cancelListener) {
            this.list = list;
            this.cancelListener = cancelListener;
        }

        @NonNull
        @Override
        public BVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BVH h, int position) {
            Booking b = list.get(position);
            h.tvName.setText(b.applianceName);
            h.tvWatt.setText(b.wattage + " W");

            if (b.type != null) {
                switch (b.type) {
                    case WASHING_MACHINE: h.ivIcon.setImageResource(R.drawable.ic_washing_machine); break;
                    case VACUUM: h.ivIcon.setImageResource(R.drawable.ic_vacuum); break;
                    case CLIM: h.ivIcon.setImageResource(R.drawable.ic_clim); break;
                    case IRON: h.ivIcon.setImageResource(R.drawable.ic_iron); break;
                }
            }

            String begin = b.beginTime.length() >= 16 ? b.beginTime.substring(11, 16) : b.beginTime;
            String end = b.endTime.length() >= 16 ? b.endTime.substring(11, 16) : b.endTime;
            String date = b.beginTime.length() >= 10 ? b.beginTime.substring(0, 10) : "";
            h.tvTime.setText(date + " · " + begin + " – " + end);

            h.tvDelta.setText((b.ecocoinsDelta >= 0 ? "+" : "") + b.ecocoinsDelta + " 🌿");
            h.tvDelta.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    Color.parseColor(b.ecocoinsDelta > 0 ? "#388E3C" : (b.ecocoinsDelta < 0 ? "#C62828" : "#9E9E9E"))));

            h.dividerCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setOnClickListener(v -> cancelListener.onCancel(b));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class BVH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName, tvTime, tvWatt, tvDelta;
            View dividerCancel;
            Button btnCancel;

            BVH(View v) {
                super(v);
                ivIcon = v.findViewById(R.id.iv_booking_icon);
                tvName = v.findViewById(R.id.tv_booking_appliance);
                tvTime = v.findViewById(R.id.tv_booking_time);
                tvWatt = v.findViewById(R.id.tv_booking_watt);
                tvDelta = v.findViewById(R.id.tv_booking_delta);
                dividerCancel = v.findViewById(R.id.divider_cancel);
                btnCancel = v.findViewById(R.id.btn_cancel_booking);
            }
        }
    }
}