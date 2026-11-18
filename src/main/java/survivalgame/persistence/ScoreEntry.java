package survivalgame.persistence;

/** Résultat immuable du classement.
 * @param name pseudonyme enregistré
 * @param score score obtenu
 */
public record ScoreEntry(String name, int score) { }
