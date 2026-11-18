package survivalgame.model;

/** Écrans et phases de l'application. */
public enum GameStatus {
    /** Écran d'accueil. */ MENU,
    /** Partie en cours. */ PLAYING,
    /** Boutique entre deux vagues. */ SHOP,
    /** Partie suspendue. */ PAUSED,
    /** Écran de fin de partie. */ GAME_OVER,
    /** Tableau des meilleurs scores. */ SCORES,
    /** Aide et commandes. */ HELP
}
