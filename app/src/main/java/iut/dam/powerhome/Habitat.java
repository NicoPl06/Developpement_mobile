package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.List;

public class Habitat {
    int HabitatID;
    String ResidentName;
    int floor;
    double area;

    List<Appliance> appliances = new ArrayList<>();

    public Habitat(int id, String ResidentName, int floor, double area, Appliance appliance){
        this.HabitatID = id;
        this.ResidentName = ResidentName;
        this.floor = floor;
        this.area = area;

        appliances.add(appliance);
    }
}
