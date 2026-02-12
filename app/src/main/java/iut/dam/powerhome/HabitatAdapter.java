package iut.dam.powerhome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HabitatAdapter extends ArrayAdapter<Habitat> {

    Activity activity;
    int itemResId;
    List<Habitat> items;

    public HabitatAdapter(Activity activity, int itemResId, List<Habitat> items) {
        super(activity, itemResId, items);
        this.activity = activity;
        this.itemResId = itemResId;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;

        if (layout == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResId, parent, false);
        }

        // Récupération des vues
        TextView tvResident = layout.findViewById(R.id.tv_resident);
        TextView tvFloor = layout.findViewById(R.id.tv_floor);
        TextView tvAppliances = layout.findViewById(R.id.tv_appliances);

        // Récupération de l'objet Habitat
        Habitat h = items.get(position);

        // Remplissage des vues
        tvResident.setText(h.ResidentName);
        tvFloor.setText("Étage : " + h.floor);
        tvAppliances.setText(h.appliances.size() + " équipement(s)");

        return layout;
    }
}
