package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.List;

public class Habitat {

    int HabitatID;
    String ResidentName;
    int floor;
    double area;

    List<Appliance> appliances = new ArrayList<>();


    public Habitat(int id, String ResidentName, int floor, double area, List<Appliance> appliances) {
        this.HabitatID = id;
        this.ResidentName = ResidentName;
        this.floor = floor;
        this.area = area;
        this.appliances = appliances;
    }
}
