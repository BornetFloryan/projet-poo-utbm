package survivalgame.persistence;

import survivalgame.model.GameState;

/** État restauré par le gestionnaire de sauvegarde.
 * @param state données de la partie
 * @param remainingToSpawn ennemis de la vague qui n'ont pas encore apparu
 */
public record SavedGame(GameState state, int remainingToSpawn) { }
