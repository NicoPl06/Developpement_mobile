package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.List;

public class Habitat {
    public int HabitatID;
    public String ResidentName;   // Nom du propriétaire
    public List<String> coNames;  // Liste des prénoms des co-résidents
    public int floor;
    public double area;
    public List<Appliance> appliances;

    public Habitat(int id, String residentName, List<String> coNames,
                   int floor, double area, List<Appliance> appliances) {
        this.HabitatID    = id;
        this.ResidentName = residentName;
        this.coNames      = (coNames != null) ? coNames : new ArrayList<>();
        this.floor        = floor;
        this.area         = area;
        this.appliances   = (appliances != null) ? appliances : new ArrayList<>();
    }

    /** * Génère le nom d'affichage type "Plaisance & Lenny"
     */
    public String getDisplayName() {
        if (coNames == null || coNames.isEmpty()) {
            return ResidentName;
        }
        // Joint le propriétaire avec les co-résidents
        return ResidentName + " & " + String.join(" & ", coNames);
    }
}