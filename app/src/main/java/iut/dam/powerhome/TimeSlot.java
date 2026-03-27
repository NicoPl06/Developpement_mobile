package iut.dam.powerhome;

public class TimeSlot {
    public int id;
    public String begin_time;
    public String end_time;
    public int percent;
    public String color;
    public int maxWattage;
    public int bookedWattage;

    public TimeSlot(int id, String begin_time, String end_time,
                    int percent, String color, int maxWattage, int bookedWattage) {
        this.id           = id;
        this.begin_time   = begin_time;
        this.end_time     = end_time;
        this.percent      = percent;
        this.color        = color;
        this.maxWattage   = maxWattage;
        this.bookedWattage = bookedWattage;
    }
}