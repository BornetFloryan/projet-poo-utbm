package survivalgame.persistence;

import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.MapGenerator;
import survivalgame.model.Player;
import survivalgame.model.Weapon;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Enregistre et restaure un point de reprise au début de la vague en cours. */
public final class GameSaveManager {
    private static final String FORMAT_VERSION = "1";
    private final Path file = Path.of("savegame.properties");

    /** Écrit la progression persistante du joueur dans un fichier lisible. */
    public void save(GameState state) throws IOException {
        Player player = state.getPlayer();
        Properties data = new Properties();
        data.setProperty("version", FORMAT_VERSION);
        data.setProperty("mapSeed", Long.toString(state.getMap().getSeed()));
        data.setProperty("wave", Integer.toString(state.getWave()));
        data.setProperty("health", Integer.toString(player.getHealth()));
        data.setProperty("score", Integer.toString(player.getScore()));
        data.setProperty("credits", Integer.toString(player.getCredits()));
        data.setProperty("weaponName", player.getWeapon().name());
        data.setProperty("weaponDamage", Integer.toString(player.getWeapon().damage()));
        data.setProperty("weaponCooldown", Integer.toString(player.getWeapon().cooldownMs()));
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            data.store(writer, "Sauvegarde de progression Survival Game 2D");
        }
    }

    /** Charge la progression et recrée la carte reproductible enregistrée. */
    public GameState load() throws IOException {
        if (!Files.isRegularFile(file)) {
            throw new IOException("Aucune sauvegarde trouvée (savegame.properties).");
        }
        Properties data = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            data.load(reader);
        }
        try {
            if (!FORMAT_VERSION.equals(required(data, "version"))) {
                throw new IllegalArgumentException("Version de sauvegarde non prise en charge.");
            }
            long seed = Long.parseLong(required(data, "mapSeed"));
            int wave = Integer.parseInt(required(data, "wave"));
            int health = Integer.parseInt(required(data, "health"));
            int score = Integer.parseInt(required(data, "score"));
            int credits = Integer.parseInt(required(data, "credits"));
            Weapon weapon = new Weapon(required(data, "weaponName"),
                    Integer.parseInt(required(data, "weaponDamage")),
                    Integer.parseInt(required(data, "weaponCooldown")));
            if (weapon.damage() < 1 || weapon.cooldownMs() < 1 || weapon.name().isBlank()) {
                throw new IllegalArgumentException("Arme invalide.");
            }
            GameState state = new GameState(new MapGenerator().generate(seed));
            state.setWave(wave);
            state.getPlayer().restoreProgress(health, score, credits, weapon);
            return state;
        } catch (IllegalArgumentException exception) {
            throw new IOException("Sauvegarde invalide : " + exception.getMessage(), exception);
        }
    }

    private String required(Properties data, String key) {
        String value = data.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Champ manquant : " + key);
        }
        return value;
    }
}
