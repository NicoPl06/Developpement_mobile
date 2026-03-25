package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

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
        bookingAdapter = new BookingAdapter(bookings);
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
                        int coins = json.optInt("ecocoins", 0);
                        tvEcocoinsBalance.setText(String.valueOf(coins));
                        tvEcocoinsBalance.setTextColor(coins >= 0 ? Color.WHITE : Color.parseColor("#EF9A9A"));

                        bookings.clear();
                        JSONArray arr = json.getJSONArray("bookings");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject b = arr.getJSONObject(i);
                            bookings.add(new Booking(
                                    b.optString("appliance_name", "Appareil"),
                                    b.optString("begin_time", ""),
                                    b.optString("end_time", ""),
                                    b.optInt("wattage", 0),
                                    b.optInt("ecocoins_delta", 0),
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

    // ─── Modèle Booking ───

    public static class Booking {
        public String applianceName;
        public String beginTime;
        public String endTime;
        public int wattage;
        public int ecocoinsDelta;
        public ApplianceType type;

        public Booking(String applianceName, String beginTime, String endTime,
                       int wattage, int ecocoinsDelta, ApplianceType type) {
            this.applianceName  = applianceName;
            this.beginTime      = beginTime;
            this.endTime        = endTime;
            this.wattage        = wattage;
            this.ecocoinsDelta  = ecocoinsDelta;
            this.type           = type;
        }
    }

    // ─── Adapter Booking ───

    public static class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BVH> {

        private final List<Booking> list;

        BookingAdapter(List<Booking> list) { this.list = list; }

        @NonNull
        @Override
        public BVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
            return new BVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BVH h, int position) {
            Booking b = list.get(position);

            h.tvName.setText(b.applianceName);
            h.tvWatt.setText(b.wattage + " W");

            // Icône
            if (b.type != null) {
                switch (b.type) {
                    case WASHING_MACHINE: h.ivIcon.setImageResource(R.drawable.ic_washing_machine); break;
                    case VACUUM:          h.ivIcon.setImageResource(R.drawable.ic_vacuum);          break;
                    case CLIM:            h.ivIcon.setImageResource(R.drawable.ic_clim);            break;
                    case IRON:            h.ivIcon.setImageResource(R.drawable.ic_iron);            break;
                    default: break;
                }
            }

            // Heure
            String begin = b.beginTime.length() >= 16 ? b.beginTime.substring(11, 16) : b.beginTime;
            String end   = b.endTime.length()   >= 16 ? b.endTime.substring(11, 16)   : b.endTime;
            String date  = b.beginTime.length() >= 10 ? b.beginTime.substring(0, 10)  : "";
            h.tvTime.setText(date + " · " + begin + " – " + end);

            // Badge delta
            if (b.ecocoinsDelta > 0) {
                h.tvDelta.setText("+" + b.ecocoinsDelta + " 🌿");
                h.tvDelta.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#388E3C")));
                h.tvDelta.setTextColor(Color.WHITE);
            } else if (b.ecocoinsDelta < 0) {
                h.tvDelta.setText(b.ecocoinsDelta + " 🌿");
                h.tvDelta.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#C62828")));
                h.tvDelta.setTextColor(Color.WHITE);
            } else {
                h.tvDelta.setText("±0 🌿");
                h.tvDelta.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#9E9E9E")));
                h.tvDelta.setTextColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class BVH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName, tvTime, tvWatt, tvDelta;
            BVH(View v) {
                super(v);
                ivIcon  = v.findViewById(R.id.iv_booking_icon);
                tvName  = v.findViewById(R.id.tv_booking_appliance);
                tvTime  = v.findViewById(R.id.tv_booking_time);
                tvWatt  = v.findViewById(R.id.tv_booking_watt);
                tvDelta = v.findViewById(R.id.tv_booking_delta);
            }
        }
    }
}