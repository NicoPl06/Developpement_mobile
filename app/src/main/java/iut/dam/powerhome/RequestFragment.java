package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestFragment extends Fragment {

    private static final String BASE_URL = "http://10.0.2.2/server/";

    private RecyclerView rvNotifications, rvRequests;
    private View llEmpty;
    private Button btnTabNotifs, btnTabRequests;
    private TextView tvNotifCount;

    private final List<NotifItem> notifList   = new ArrayList<>();
    private final List<JoinRequest> reqList   = new ArrayList<>();
    private NotifAdapter notifAdapter;
    private JoinRequestAdapter reqAdapter;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rv_notifications);
        rvRequests      = view.findViewById(R.id.rv_requests);
        llEmpty         = view.findViewById(R.id.ll_empty_notif);
        btnTabNotifs    = view.findViewById(R.id.btn_tab_notifs);
        btnTabRequests  = view.findViewById(R.id.btn_tab_requests);
        tvNotifCount    = view.findViewById(R.id.tv_notif_count);

        SharedPreferences prefs = requireContext().getSharedPreferences("SESSIONS", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notifAdapter = new NotifAdapter(notifList);
        rvNotifications.setAdapter(notifAdapter);

        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        reqAdapter = new JoinRequestAdapter(reqList, this::respondToRequest);
        rvRequests.setAdapter(reqAdapter);

        btnTabNotifs.setOnClickListener(v -> showTab(true));
        btnTabRequests.setOnClickListener(v -> showTab(false));

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle("Mes notifications");

        loadNotifications();
    }

    private void showTab(boolean notifTab) {
        int accent = ColorManager.getColor(requireContext(), userId);
        if (notifTab) {
            rvNotifications.setVisibility(View.VISIBLE);
            rvRequests.setVisibility(View.GONE);
            btnTabNotifs.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
            btnTabNotifs.setTextColor(Color.WHITE);
            btnTabRequests.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            btnTabRequests.setTextColor(Color.parseColor("#888888"));
        } else {
            rvNotifications.setVisibility(View.GONE);
            rvRequests.setVisibility(View.VISIBLE);
            btnTabRequests.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
            btnTabRequests.setTextColor(Color.WHITE);
            btnTabNotifs.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            btnTabNotifs.setTextColor(Color.parseColor("#888888"));
        }
    }

    private void loadNotifications() {
        if (userId <= 0) return;
        String url = BASE_URL + "getNotifications.php?user_id=" + userId;
        Volley.newRequestQueue(requireContext()).add(new StringRequest(url, response -> {
            if (!isAdded()) return;
            try {
                JSONObject json = new JSONObject(response);

                // Notifications
                notifList.clear();
                JSONArray notifs = json.getJSONArray("notifications");
                for (int i = 0; i < notifs.length(); i++) {
                    JSONObject n = notifs.getJSONObject(i);
                    notifList.add(new NotifItem(
                            n.optString("message", ""),
                            n.optString("created_at", ""),
                            n.optInt("is_read", 0) == 0
                    ));
                }

                // Demandes
                reqList.clear();
                JSONArray reqs = json.getJSONArray("requests");
                for (int i = 0; i < reqs.length(); i++) {
                    JSONObject r = reqs.getJSONObject(i);
                    reqList.add(new JoinRequest(
                            r.optInt("id", 0),
                            r.optString("firstname", "") + " " + r.optString("lastname", ""),
                            r.optInt("habitat_id", 0),
                            r.optInt("floor", 0),
                            r.optDouble("area", 0)
                    ));
                }

                notifAdapter.notifyDataSetChanged();
                reqAdapter.notifyDataSetChanged();

                // Badge demandes en attente
                if (!reqList.isEmpty()) {
                    tvNotifCount.setVisibility(View.VISIBLE);
                    tvNotifCount.setText(String.valueOf(reqList.size()));
                } else {
                    tvNotifCount.setVisibility(View.GONE);
                }

                // État vide
                boolean empty = notifList.isEmpty();
                llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                rvNotifications.setVisibility(empty ? View.GONE : View.VISIBLE);

            } catch (JSONException e) { e.printStackTrace(); }
        }, error -> Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show()));
    }

    private void respondToRequest(JoinRequest req, boolean accept) {
        String url = BASE_URL + "respondJoinRequest.php";
        StringRequest post = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if ("success".equals(json.optString("status"))) {
                            String msg = accept ? "Demande acceptée ✓" : "Demande refusée";
                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            reqList.remove(req);
                            reqAdapter.notifyDataSetChanged();
                            if (reqList.isEmpty()) tvNotifCount.setVisibility(View.GONE);
                        }
                    } catch (JSONException ignored) {}
                }, error -> Toast.makeText(getContext(), "Erreur", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("owner_id",   String.valueOf(userId));
                p.put("request_id", String.valueOf(req.id));
                p.put("response",   accept ? "accepted" : "refused");
                return p;
            }
        };
        Volley.newRequestQueue(requireContext()).add(post);
    }

    // ────────── Modèles ──────────

    public static class NotifItem {
        public String message, createdAt;
        public boolean isUnread;
        NotifItem(String m, String t, boolean u) { message=m; createdAt=t; isUnread=u; }
    }

    public static class JoinRequest {
        public int id, habitatId, floor;
        public String requesterName;
        public double area;
        JoinRequest(int id, String name, int hId, int floor, double area) {
            this.id=id; this.requesterName=name.trim(); this.habitatId=hId; this.floor=floor; this.area=area;
        }
    }

    // ────────── Adapter Notifications ──────────

    public static class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NVH> {
        private final List<NotifItem> list;
        NotifAdapter(List<NotifItem> list) { this.list = list; }

        @NonNull @Override
        public NVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull NVH h, int pos) {
            NotifItem n = list.get(pos);
            h.tvMessage.setText(n.message);

            // Formater la date : "2026-03-27 14:30:00" → "27/03 à 14:30"
            String time = n.createdAt;
            if (time.length() >= 16) {
                time = time.substring(8,10) + "/" + time.substring(5,7) + " à " + time.substring(11,16);
            }
            h.tvTime.setText(time);

            // Non-lue : fond légèrement coloré
            h.itemView.setAlpha(n.isUnread ? 1f : 0.75f);
        }

        @Override public int getItemCount() { return list.size(); }

        static class NVH extends RecyclerView.ViewHolder {
            TextView tvMessage, tvTime;
            NVH(View v) {
                super(v);
                tvMessage = v.findViewById(R.id.tv_notif_message);
                tvTime    = v.findViewById(R.id.tv_notif_time);
            }
        }
    }

    // ────────── Adapter Demandes ──────────

    public interface OnRespondListener {
        void onRespond(JoinRequest req, boolean accept);
    }

    public static class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.JRVH> {
        private final List<JoinRequest> list;
        private final OnRespondListener listener;
        JoinRequestAdapter(List<JoinRequest> list, OnRespondListener l) { this.list=list; this.listener=l; }

        @NonNull @Override
        public JRVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_join_request, parent, false);
            return new JRVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull JRVH h, int pos) {
            JoinRequest r = list.get(pos);
            h.tvName.setText(r.requesterName);
            h.tvInfo.setText("souhaite rejoindre votre habitat · Étage " + r.floor + " · " + r.area + " m²");
            h.btnAccept.setOnClickListener(v -> listener.onRespond(r, true));
            h.btnRefuse.setOnClickListener(v -> listener.onRespond(r, false));
        }

        @Override public int getItemCount() { return list.size(); }

        static class JRVH extends RecyclerView.ViewHolder {
            TextView tvName, tvInfo;
            Button btnAccept, btnRefuse;
            JRVH(View v) {
                super(v);
                tvName    = v.findViewById(R.id.tv_request_name);
                tvInfo    = v.findViewById(R.id.tv_request_info);
                btnAccept = v.findViewById(R.id.btn_accept);
                btnRefuse = v.findViewById(R.id.btn_refuse);
            }
        }
    }
}