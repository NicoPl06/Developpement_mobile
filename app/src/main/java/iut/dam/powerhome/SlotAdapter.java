package iut.dam.powerhome;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(TimeSlot slot);
    }

    private final List<TimeSlot> slots;
    private final OnBookClickListener listener;

    public SlotAdapter(List<TimeSlot> slots, OnBookClickListener listener) {
        this.slots    = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeslot, parent, false);
        return new SlotViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder h, int position) {
        TimeSlot slot = slots.get(position);

        // Formatage heure : "2026-06-20 08:00:00" → "08:00 – 09:00"
        String begin = slot.begin_time.length() >= 16 ? slot.begin_time.substring(11, 16) : slot.begin_time;
        String end   = slot.end_time.length()   >= 16 ? slot.end_time.substring(11, 16)   : slot.end_time;
        h.tvTime.setText(begin + " – " + end);
        h.tvLoad.setText("Charge : " + slot.percent + " %");

        // ProgressBar et couleur
        h.pbSlot.setProgress(slot.percent);
        h.tvPercent.setText(slot.percent + " %");

        int barColor;
        int barBg;
        switch (slot.color) {
            case "green":
                barColor = Color.parseColor("#388E3C");
                barBg    = Color.parseColor("#C8E6C9");
                break;
            case "orange":
                barColor = Color.parseColor("#E65100");
                barBg    = Color.parseColor("#FFE0B2");
                break;
            default: // red
                barColor = Color.parseColor("#C62828");
                barBg    = Color.parseColor("#FFCDD2");
                break;
        }

        // Barre de couleur à gauche
        h.colorBar.setBackgroundColor(barColor);

        // Tint de la ProgressBar
        h.pbSlot.setProgressTintList(android.content.res.ColorStateList.valueOf(barColor));
        h.pbSlot.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(barBg));

        h.tvPercent.setTextColor(barColor);
        h.tvLoad.setTextColor(barColor);

        h.btnBook.setOnClickListener(v -> listener.onBookClick(slot));
    }

    @Override
    public int getItemCount() { return slots.size(); }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvLoad, tvPercent;
        ProgressBar pbSlot;
        View colorBar;
        Button btnBook;

        SlotViewHolder(View v) {
            super(v);
            tvTime    = v.findViewById(R.id.tv_slot_time);
            tvLoad    = v.findViewById(R.id.tv_slot_load);
            tvPercent = v.findViewById(R.id.tv_slot_percent);
            pbSlot    = v.findViewById(R.id.pb_slot);
            colorBar  = v.findViewById(R.id.view_color_bar);
            btnBook   = v.findViewById(R.id.btn_book);
        }
    }
}