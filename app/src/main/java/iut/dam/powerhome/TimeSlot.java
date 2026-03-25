package iut.dam.powerhome;

public class TimeSlot {
    public int id;
    public String begin_time;  // "2026-06-20 08:00:00"
    public String end_time;
    public int percent;        // 0 à 100
    public String color;       // "green", "orange", "red"
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