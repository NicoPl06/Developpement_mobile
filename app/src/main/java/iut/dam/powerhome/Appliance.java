package iut.dam.powerhome;

public class Appliance {
    int ID;
    String Name;
    String reference;
    int wattage;

    public Appliance(int ID, String name, String reference, int wattage){
        this.ID = ID;
        this.Name = name;
        this.reference = reference;
        this.wattage = wattage;
    }
}
