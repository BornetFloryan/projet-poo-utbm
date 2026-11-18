package survivalgame.persistence;

import survivalgame.model.Barricade;
import survivalgame.model.Enemy;
import survivalgame.model.FastZombie;
import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.MapGenerator;
import survivalgame.model.NormalZombie;
import survivalgame.model.Player;
import survivalgame.model.Projectile;
import survivalgame.model.Weapon;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

/**
 * Persiste une partie complète dans le fichier savegame.properties.  *
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
public final class GameSaveManager {
    private static final String FILE_NAME = "savegame.properties";
    private static final String VERSION = "2";
    private static final int MAX_ENEMIES = 14;
    private static final int MAX_PROJECTILES = 100;
    private final Path file = Path.of(FILE_NAME);

    /** Crée un gestionnaire qui utilise le dossier courant. */
    public GameSaveManager() { }

    /** Enregistre tous les éléments nécessaires à la reprise de la partie.
     * @param state état de partie à sauvegarder
     * @param remainingToSpawn nombre d'ennemis restant à faire apparaître
     * @throws IOException si l'état ne peut pas être écrit sur disque
     */
    public void save(GameState state, int remainingToSpawn) throws IOException {
        if (state == null || state.getPlayer() == null || state.getMap() == null) {
            throw new IOException("Aucune partie à sauvegarder.");
        }
        if (remainingToSpawn < 0 || remainingToSpawn > 32
                || state.getEnemies().size() > MAX_ENEMIES
                || state.getProjectiles().size() > MAX_PROJECTILES) {
            throw new IOException("L'état de partie dépasse les limites de sauvegarde.");
        }

        Properties data = new Properties();
        Player player = state.getPlayer();
        data.setProperty("version", VERSION);
        data.setProperty("map.seed", Long.toString(state.getMap().getSeed()));
        data.setProperty("wave", Integer.toString(state.getWave()));
        data.setProperty("wave.remaining", Integer.toString(remainingToSpawn));
        data.setProperty("player.x", Double.toString(player.getX()));
        data.setProperty("player.y", Double.toString(player.getY()));
        data.setProperty("player.angle", Double.toString(player.getFacingAngle()));
        data.setProperty("player.health", Integer.toString(player.getHealth()));
        data.setProperty("player.score", Integer.toString(player.getScore()));
        data.setProperty("player.credits", Integer.toString(player.getCredits()));
        data.setProperty("weapon.name", player.getWeapon().name());
        data.setProperty("weapon.damage", Integer.toString(player.getWeapon().damage()));
        data.setProperty("weapon.cooldown", Integer.toString(player.getWeapon().cooldownMs()));

        List<GameMap.SpawnPoint> windows = windowEntrances(state.getMap());
        data.setProperty("barricades.count", Integer.toString(windows.size()));
        for (int i = 0; i < windows.size(); i++) {
            data.setProperty("barricades." + i,
                    Integer.toString(barricadeDurability(state.getMap(), windows.get(i))));
        }

        data.setProperty("enemies.count", Integer.toString(state.getEnemies().size()));
        for (int i = 0; i < state.getEnemies().size(); i++) {
            writeEnemy(data, i, state.getEnemies().get(i));
        }

        data.setProperty("projectiles.count", Integer.toString(state.getProjectiles().size()));
        for (int i = 0; i < state.getProjectiles().size(); i++) {
            writeProjectile(data, i, state.getProjectiles().get(i));
        }

        writeAtomically(data);
    }

    /** Charge et valide une partie complète.
     * @return partie restaurée et nombre d'apparitions restantes
     * @throws IOException si le fichier est absent, illisible ou invalide
     */
    public SavedGame load() throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("Aucune sauvegarde trouvée (" + FILE_NAME + ").");
        }

        Properties data = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            data.load(reader);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Format de sauvegarde illisible.", exception);
        }

        try {
            if (!VERSION.equals(required(data, "version"))) {
                throw new IllegalArgumentException("Version de sauvegarde inconnue.");
            }
            long seed = longValue(data, "map.seed");
            int wave = positiveInt(data, "wave");
            int remainingToSpawn = boundedInt(data, "wave.remaining", 0, 32);
            GameMap map = new MapGenerator().generate(seed);
            GameState state = new GameState(map);
            state.setWave(wave);
            restoreBarricades(data, map);
            restorePlayer(data, state.getPlayer(), map);
            restoreEnemies(data, state);
            restoreProjectiles(data, state, map);
            return new SavedGame(state, remainingToSpawn);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Sauvegarde invalide : " + exception.getMessage(), exception);
        }
    }

    /** Écrit les propriétés dans un fichier temporaire puis remplace la sauvegarde actuelle.
     * @param data propriétés à enregistrer
     * @throws IOException si l'écriture ou le remplacement échoue
     */
    private void writeAtomically(Properties data) throws IOException {
        Path absoluteFile = file.toAbsolutePath();
        Path directory = absoluteFile.getParent();
        Path temporaryFile = Files.createTempFile(directory, "savegame-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                data.store(writer, "Sauvegarde de Survival Game 2D");
            }
            try {
                Files.move(temporaryFile, absoluteFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, absoluteFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Sérialise un ennemi avec son type, sa position et sa vie.
     * @param data propriétés de sauvegarde
     * @param index numéro de l'ennemi
     * @param enemy ennemi à enregistrer
     */
    private void writeEnemy(Properties data, int index, Enemy enemy) {
        String prefix = "enemies." + index + ".";
        data.setProperty(prefix + "type", enemy instanceof FastZombie ? "fast" : "normal");
        data.setProperty(prefix + "x", Double.toString(enemy.getX()));
        data.setProperty(prefix + "y", Double.toString(enemy.getY()));
        data.setProperty(prefix + "angle", Double.toString(enemy.getFacingAngle()));
        data.setProperty(prefix + "health", Integer.toString(enemy.getHealth()));
    }

    /** Sérialise un projectile actif avec sa vitesse et sa portée restante.
     * @param data propriétés de sauvegarde
     * @param index numéro du projectile
     * @param projectile projectile à enregistrer
     */
    private void writeProjectile(Properties data, int index, Projectile projectile) {
        String prefix = "projectiles." + index + ".";
        data.setProperty(prefix + "x", Double.toString(projectile.getX()));
        data.setProperty(prefix + "y", Double.toString(projectile.getY()));
        data.setProperty(prefix + "velocityX", Double.toString(projectile.getVelocityX()));
        data.setProperty(prefix + "velocityY", Double.toString(projectile.getVelocityY()));
        data.setProperty(prefix + "damage", Integer.toString(projectile.getDamage()));
        data.setProperty(prefix + "range", Double.toString(projectile.getRemainingRange()));
    }

    /** Restaure la progression et la position du joueur depuis les propriétés.
     * @param data propriétés chargées
     * @param player joueur à modifier
     * @param map carte régénérée
     */
    private void restorePlayer(Properties data, Player player, GameMap map) {
        double x = finiteDouble(data, "player.x");
        double y = finiteDouble(data, "player.y");
        double angle = finiteDouble(data, "player.angle");
        requireInsideMap(x, y, map, "joueur");
        int health = boundedInt(data, "player.health", 1, player.getMaxHealth());
        int score = boundedInt(data, "player.score", 0, Integer.MAX_VALUE);
        int credits = boundedInt(data, "player.credits", 0, Integer.MAX_VALUE);
        String weaponName = required(data, "weapon.name");
        int damage = positiveInt(data, "weapon.damage");
        int cooldown = positiveInt(data, "weapon.cooldown");
        player.restoreProgress(health, score, credits, new Weapon(weaponName, damage, cooldown));
        player.setPosition(x, y);
        player.setFacingAngle(angle);
    }

    /** Restaure la liste des ennemis validés depuis une sauvegarde.
     * @param data propriétés chargées
     * @param state partie à compléter
     */
    private void restoreEnemies(Properties data, GameState state) {
        int count = boundedInt(data, "enemies.count", 0, MAX_ENEMIES);
        for (int i = 0; i < count; i++) {
            String prefix = "enemies." + i + ".";
            double x = finiteDouble(data, prefix + "x");
            double y = finiteDouble(data, prefix + "y");
            double angle = finiteDouble(data, prefix + "angle");
            requireInsideMap(x, y, state.getMap(), "zombie");
            String type = required(data, prefix + "type");
            Enemy enemy = switch (type) {
                case "normal" -> new NormalZombie(x, y);
                case "fast" -> new FastZombie(x, y);
                default -> throw new IllegalArgumentException("Type de zombie inconnu.");
            };
            int health = boundedInt(data, prefix + "health", 1, enemy.getMaxHealth());
            enemy.takeDamage(enemy.getMaxHealth() - health);
            enemy.setFacingAngle(angle);
            state.getEnemies().add(enemy);
        }
    }

    /** Restaure les projectiles actifs depuis une sauvegarde.
     * @param data propriétés chargées
     * @param state partie à compléter
     * @param map carte régénérée
     */
    private void restoreProjectiles(Properties data, GameState state, GameMap map) {
        int count = boundedInt(data, "projectiles.count", 0, MAX_PROJECTILES);
        for (int i = 0; i < count; i++) {
            String prefix = "projectiles." + i + ".";
            double x = finiteDouble(data, prefix + "x");
            double y = finiteDouble(data, prefix + "y");
            requireInsideMap(x, y, map, "projectile");
            double velocityX = finiteDouble(data, prefix + "velocityX");
            double velocityY = finiteDouble(data, prefix + "velocityY");
            double range = boundedDouble(data, prefix + "range", 0, 520);
            int damage = positiveInt(data, prefix + "damage");
            state.getProjectiles().add(new Projectile(x, y, velocityX, velocityY, damage, range));
        }
    }

    /** Restaure l'intégrité et la résistance de toutes les barricades.
     * @param data propriétés chargées
     * @param map carte régénérée
     */
    private void restoreBarricades(Properties data, GameMap map) {
        List<GameMap.SpawnPoint> windows = windowEntrances(map);
        int count = boundedInt(data, "barricades.count", 0, GameMap.COLUMNS + GameMap.ROWS);
        if (count != windows.size()) {
            throw new IllegalArgumentException("Le nombre de fenêtres ne correspond pas à la carte.");
        }
        for (int i = 0; i < windows.size(); i++) {
            int durability = boundedInt(data, "barricades." + i, 0, 4);
            int[] tile = barricadeTile(windows.get(i));
            if (!map.hasBarricade(tile[0], tile[1])) {
                throw new IllegalArgumentException("Barricade absente de la carte régénérée.");
            }
            map.damageBarricade(tile[0], tile[1], 4 - durability);
        }
    }

    /** Retourne la durabilité restante d'une barricade devant une fenêtre.
     * @param map carte contenant la barricade
     * @param window entrée de fenêtre concernée
     * @return durabilité comprise entre zéro et quatre
     */
    private int barricadeDurability(GameMap map, GameMap.SpawnPoint window) {
        int[] tile = barricadeTile(window);
        return map.getObstacles().stream()
                .filter(Barricade.class::isInstance)
                .map(Barricade.class::cast)
                .filter(barricade -> barricade.getColumn() == tile[0]
                        && barricade.getRow() == tile[1])
                .mapToInt(Barricade::getDurability)
                .findFirst().orElse(0);
    }

    /** Filtre les entrées de carte correspondant aux fenêtres extérieures.
     * @param map carte examinée
     * @return liste des points de fenêtre
     */
    private List<GameMap.SpawnPoint> windowEntrances(GameMap map) {
        return map.getEntrances().stream()
                .filter(entrance -> entrance.type() == GameMap.SpawnType.WINDOW)
                .toList();
    }

    /** Convertit une fenêtre en coordonnées de sa barricade intérieure.
     * @param window point d'apparition de fenêtre
     * @return colonne et ligne de la barricade
     */
    private int[] barricadeTile(GameMap.SpawnPoint window) {
        if (window.row() == 0) return new int[]{window.column(), 1};
        if (window.row() == GameMap.ROWS - 1) {
            return new int[]{window.column(), GameMap.ROWS - 2};
        }
        if (window.column() == 0) return new int[]{1, window.row()};
        return new int[]{GameMap.COLUMNS - 2, window.row()};
    }

    /** Vérifie qu'une position reste à l'intérieur de la carte.
     * @param x abscisse
     * @param y ordonnée
     * @param map carte à contrôler
     * @param name nom de l'entité utilisé dans le message d'erreur
     */
    private void requireInsideMap(double x, double y, GameMap map, String name) {
        if (x < 0 || y < 0 || x >= map.getPixelWidth() || y >= map.getPixelHeight()) {
            throw new IllegalArgumentException("Position de " + name + " hors de la carte.");
        }
    }

    /** Lit un entier strictement positif dans les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @return valeur entière positive
     */
    private int positiveInt(Properties data, String key) {
        return boundedInt(data, key, 1, Integer.MAX_VALUE);
    }

    /** Lit un entier borné dans les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @param minimum borne minimale incluse
     * @param maximum borne maximale incluse
     * @return valeur entière validée
     */
    private int boundedInt(Properties data, String key, int minimum, int maximum) {
        int value;
        try {
            value = Integer.parseInt(required(data, key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valeur numérique invalide : " + key, exception);
        }
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException("Valeur hors limites : " + key);
        }
        return value;
    }

    /** Lit un entier long dans les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @return valeur longue validée
     */
    private long longValue(Properties data, String key) {
        try {
            return Long.parseLong(required(data, key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valeur numérique invalide : " + key, exception);
        }
    }

    /** Lit un nombre décimal fini dans les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @return nombre décimal fini
     */
    private double finiteDouble(Properties data, String key) {
        double value;
        try {
            value = Double.parseDouble(required(data, key));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Valeur numérique invalide : " + key, exception);
        }
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Valeur non finie : " + key);
        }
        return value;
    }

    /** Lit un nombre décimal borné dans les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @param minimum borne minimale incluse
     * @param maximum borne maximale incluse
     * @return nombre décimal validé
     */
    private double boundedDouble(Properties data, String key, double minimum, double maximum) {
        double value = finiteDouble(data, key);
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException("Valeur hors limites : " + key);
        }
        return value;
    }

    /** Retourne une valeur textuelle non vide depuis les propriétés.
     * @param data propriétés chargées
     * @param key clé recherchée
     * @return valeur texte validée
     */
    private String required(Properties data, String key) {
        String value = data.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Champ manquant : " + key);
        }
        return value;
    }
}
