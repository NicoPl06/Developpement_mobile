package iut.dam.powerhome;
public enum ApplianceType {
    WASHING_MACHINE, VACUUM, CLIM, IRON;

    public static ApplianceType valueOfName(String name) {
        if (name == null) return null;
        String n = name.trim().toLowerCase();

        if (n.contains("machine") || n.contains("washing")) return WASHING_MACHINE;
        if (n.contains("aspirateur") || n.contains("vacuum") || n.contains("dyson")) return VACUUM;
        if (n.contains("clim")) return CLIM;
        if (n.contains("fer") || n.contains("iron") || n.contains("repasser")) return IRON;

        return null;
    }
}