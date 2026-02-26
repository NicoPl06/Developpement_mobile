package iut.dam.powerhome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class HabitatAdapter extends ArrayAdapter<Habitat> {

    Activity activity;
    int itemResId;
    List<Habitat> items;

    public HabitatAdapter(Activity activity, int itemResId, List<Habitat> items) {
        super(activity, itemResId, items);
        this.activity  = activity;
        this.itemResId = itemResId;
        this.items     = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = convertView;
        if (layout == null) {
            layout = activity.getLayoutInflater().inflate(itemResId, parent, false);
        }

        TextView tvResident  = layout.findViewById(R.id.tv_residentName);
        LinearLayout tvFloorBlock = layout.findViewById(R.id.ll_floor_block);
        TextView tvFloorLabel = (TextView) tvFloorBlock.getChildAt(0);
        TextView tvFloorID    = (TextView) tvFloorBlock.getChildAt(1);
        TextView tvEquipements = layout.findViewById(R.id.tv_equipments);
        LinearLayout layoutIcons = layout.findViewById(R.id.ll_appliance_icons);

        Habitat h = items.get(position);

        tvResident.setText(h.ResidentName);
        tvFloorID.setText(String.valueOf(h.floor));
        tvFloorLabel.setText("ETAGE");

        // ---- Couleur dynamique sur le badge étage ----
        int accentColor = ColorManager.getColor(activity);
        tvFloorID.setBackgroundColor(accentColor);

        int count = h.appliances.size();
        tvEquipements.setText(count + " " + (count > 1 ? "équipements" : "équipement"));

        layoutIcons.removeAllViews();
        for (Appliance a : h.appliances) {
            ImageView icon = new ImageView(activity);
            switch (a.type) {
                case WASHING_MACHINE: icon.setImageResource(R.drawable.ic_washing_machine); break;
                case VACUUM:          icon.setImageResource(R.drawable.ic_vacuum);          break;
                case CLIM:            icon.setImageResource(R.drawable.ic_clim);            break;
                case IRON:            icon.setImageResource(R.drawable.ic_iron);            break;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(60, 60);
            params.setMargins(8, 0, 8, 0);
            icon.setLayoutParams(params);
            layoutIcons.addView(icon);
        }

        return layout;
    }
}