package survivalgame.persistence;

/**
 * Résultat immuable du classement.
 * @param name pseudonyme enregistré
 * @param score score obtenu
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public record ScoreEntry(String name, int score) { }
