package survivalgame.model;

/** Paramètres immuables d'une arme. */
public record Weapon(String name, int damage, int cooldownMs) {
    public static Weapon pistol() { return new Weapon("Pistolet", 16, 260); }
    public static Weapon smg() { return new Weapon("Mitraillette", 9, 115); }
    public static Weapon rifle() { return new Weapon("Fusil", 32, 520); }
}
