package iut.dam.powerhome;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ApplianceAdapter extends RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder> {

    private List<Appliance> appliances;

    public ApplianceAdapter(List<Appliance> appliances) {
        this.appliances = appliances;
    }

    @NonNull
    @Override
    public ApplianceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appliance, parent, false);
        return new ApplianceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplianceViewHolder holder, int position) {
        Appliance appliance = appliances.get(position);
        holder.txtWatt.setText(appliance.wattage + " W");

        int iconRes = R.drawable.ic_launcher_foreground;
        int nameRes = R.string.device_default;

        if (appliance.type != null) {
            switch (appliance.type) {
                case IRON: iconRes = R.drawable.ic_iron; nameRes = R.string.device_iron; break;
                case WASHING_MACHINE: iconRes = R.drawable.ic_washing_machine; nameRes = R.string.device_washing_machine; break;
                case VACUUM: iconRes = R.drawable.ic_vacuum; nameRes = R.string.device_vacuum; break;
                case CLIM: iconRes = R.drawable.ic_clim; nameRes = R.string.device_clim; break;
            }
        }

        holder.icon.setImageResource(iconRes);
        holder.txtName.setText(holder.itemView.getContext().getString(nameRes));
    }

    @Override
    public int getItemCount() {
        return appliances.size();
    }

    static class ApplianceViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtWatt;
        ImageView icon;

        ApplianceViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtWatt = itemView.findViewById(R.id.txtWatt);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}