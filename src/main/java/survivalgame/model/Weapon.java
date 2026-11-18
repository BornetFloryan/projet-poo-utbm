package survivalgame.model;

/** Paramètres immuables d'une arme.
 * @param name nom affiché
 * @param damage dégâts par projectile
 * @param cooldownMs délai entre deux tirs en millisecondes
 */
public record Weapon(String name, int damage, int cooldownMs) {
    /** Retourne l'arme de départ.
     * @return pistolet standard
     */
    public static Weapon pistol() { return new Weapon("Pistolet", 16, 260); }
    /** Retourne l'arme rapide.
     * @return mitraillette
     */
    public static Weapon smg() { return new Weapon("Mitraillette", 9, 115); }
    /** Retourne l'arme puissante.
     * @return fusil
     */
    public static Weapon rifle() { return new Weapon("Fusil", 32, 520); }
}
