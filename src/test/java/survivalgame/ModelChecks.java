package survivalgame;

import survivalgame.controller.GameEngine;
import survivalgame.model.Enemy;
import survivalgame.model.FastZombie;
import survivalgame.model.GameMap;
import survivalgame.model.GameState;
import survivalgame.model.MapGenerator;
import survivalgame.model.NormalZombie;
import survivalgame.model.Player;
import survivalgame.model.Projectile;
import survivalgame.model.ShopManager;
import survivalgame.model.Weapon;
import survivalgame.persistence.GameSaveManager;
import survivalgame.persistence.ScoreManager;
import java.awt.geom.Point2D;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Petites vérifications du modèle, sans bibliothèque de test externe. */
public final class ModelChecks {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        checkMapGeneration();
        checkShopTransactions();
        checkEnemyTypes();
        checkGameEngine();
        checkScorePersistence();
        checkGameSave();
        checkSpriteAtlas();
        System.out.println(checks + " vérifications réussies.");
    }

    private static void checkMapGeneration() {
        MapGenerator generator = new MapGenerator();
        for (long seed = 0; seed < 40; seed++) {
            GameMap map = generator.generate(seed);
            int[][] distances = map.distanceField(GameMap.COLUMNS / 2, GameMap.ROWS / 2);
            boolean connected = true;
            for (int row = 1; row < GameMap.ROWS - 1; row++) {
                for (int column = 1; column < GameMap.COLUMNS - 1; column++) {
                    if (!map.isWall(column, row) && distances[row][column] < 0) connected = false;
                }
            }
            check(connected, "accessibilité de la carte " + seed);
            check(map.getSeed() == seed, "graine conservée");
            check(map.getEntrances().size() == 5, "quatre fenêtres et une échelle");
        }
        GameMap map = generator.generate(14);
        for (GameMap.SpawnPoint entrance : map.getEntrances()) {
            if (entrance.type() == GameMap.SpawnType.LADDER) continue;
            int column = entrance.column();
            int row = entrance.row();
            if (row == 0) row++;
            else if (row == GameMap.ROWS - 1) row--;
            else if (column == 0) column++;
            else column--;
            check(map.hasBarricade(column, row), "fenêtre protégée par une barricade");
            for (int i = 0; i < 4; i++) map.damageBarricade(column, row, 1);
            check(!map.hasBarricade(column, row), "barricade destructible");
        }
    }

    private static void checkShopTransactions() {
        GameMap map = new MapGenerator().generate(2);
        GameState state = new GameState(map);
        ShopManager shop = new ShopManager();
        check(shop.buy(state, ShopManager.Item.SMG).contains("insuffisants"), "fonds insuffisants refusés");
        state.getPlayer().addReward(0, 100);
        check(shop.buy(state, ShopManager.Item.SMG).contains("effectué"), "achat réussi");
        check(state.getPlayer().getCredits() == 65, "débit des crédits correct");
        check(state.getPlayer().getWeapon().name().equals("Mitraillette"), "arme équipée");
        state.getPlayer().takeDamage(50);
        shop.buy(state, ShopManager.Item.HEAL);
        check(state.getPlayer().getHealth() == 90, "soin plafonné et appliqué");
    }

    private static void checkEnemyTypes() {
        GameState state = new GameState(new MapGenerator().generate(3));
        state.getEnemies().add(new NormalZombie(100, 100));
        state.getEnemies().add(new FastZombie(120, 100));
        check(state.getEnemies().get(0) instanceof Enemy, "zombie normal est un Enemy");
        check(state.getEnemies().get(1) instanceof Enemy, "zombie rapide est un Enemy");
    }

    private static void checkGameEngine() {
        GameEngine engine = new GameEngine();
        engine.startNewGame();
        double initialY = engine.getState().getPlayer().getY();
        engine.update(0, -1, new Point2D.Double(500, 224), false);
        check(engine.getState().getPlayer().getY() < initialY, "déplacement vers le haut");
        check(Math.abs(engine.getState().getPlayer().getFacingAngle()) < 0.01,
                "joueur orienté vers la visée");

        GameState mapState = engine.getState();
        long firstSeed = mapState.getMap().getSeed();
        var firstEntrances = mapState.getMap().getEntrances();
        mapState.getPlayer().addReward(50, 90);
        mapState.getPlayer().takeDamage(20);
        engine.startNextWave();
        check(mapState.getMap().getSeed() == firstSeed, "carte conservée jusqu'à la troisième manche");
        engine.startNextWave();
        check(mapState.getWave() == 3, "troisième manche démarrée");
        check(mapState.getMap().getSeed() != firstSeed, "nouvelle carte à la troisième manche");
        check(!mapState.getMap().getEntrances().equals(firstEntrances), "points d'apparition renouvelés");
        check(mapState.getPlayer().getScore() == 50 && mapState.getPlayer().getCredits() == 90,
                "progression conservée entre les cartes");
        check(mapState.getPlayer().getHealth() == 80, "vie conservée entre les cartes");

        GameState state = engine.getState();
        Player player = state.getPlayer();
        player.setPosition(320, 224);
        state.getEnemies().clear();
        state.getProjectiles().clear();
        FastZombie target = new FastZombie(360, 224);
        state.getEnemies().add(target);
        state.getProjectiles().add(new Projectile(340, 224, 8, 0, 32));
        engine.update(0, 0, new Point2D.Double(400, 224), false);
        check(!state.getEnemies().contains(target), "projectile élimine un zombie");
        check(Math.abs(target.getFacingAngle()) > 0.5, "zombie orienté vers sa trajectoire: "
                + target.getFacingAngle());
        check(player.getScore() == 70 && player.getCredits() == 102,
                "récompenses attribuées sans perdre la progression");
    }

    private static void checkScorePersistence() throws Exception {
        Files.writeString(Path.of("highscores.txt"), "Zoé\t80\nligne invalide\nSam\t120\n");
        ScoreManager scores = new ScoreManager();
        check(scores.getTopScores().get(0).score() == 120, "scores triés et ligne invalide ignorée");
        scores.addScore("Testeur", 200);
        check(scores.getTopScores().get(0).name().equals("Testeur"), "sauvegarde de score");
        boolean invalidNameRejected = false;
        try { scores.addScore("\t", 4); }
        catch (IllegalArgumentException exception) { invalidNameRejected = true; }
        check(invalidNameRejected, "pseudonyme invalide refusé");
    }

    private static void checkGameSave() throws Exception {
        GameState original = new GameState(new MapGenerator().generate(9182));
        original.setWave(4);
        original.getPlayer().addReward(125, 42);
        original.getPlayer().setWeapon(Weapon.smg());
        original.getPlayer().takeDamage(30);

        GameSaveManager manager = new GameSaveManager();
        manager.save(original);
        GameState restored = manager.load();
        check(restored.getWave() == 4, "vague restaurée");
        check(restored.getMap().getSeed() == 9182, "graine de carte restaurée");
        check(restored.getMap().getEntrances().equals(original.getMap().getEntrances()),
                "carte reproductible restaurée");
        check(restored.getPlayer().getHealth() == 70, "vie restaurée");
        check(restored.getPlayer().getScore() == 125, "score restauré");
        check(restored.getPlayer().getCredits() == 42, "crédits restaurés");
        check(restored.getPlayer().getWeapon().equals(Weapon.smg()), "arme restaurée");
    }

    private static void checkSpriteAtlas() throws Exception {
        try (var input = ModelChecks.class.getResourceAsStream("/images/arena-atlas.png")) {
            check(input != null, "atlas d'images inclus dans les ressources");
            var atlas = ImageIO.read(input);
            check(atlas.getWidth() >= 1700 && atlas.getHeight() >= 800, "dimensions de l'atlas valides");
            check(atlas.getColorModel().hasAlpha(), "transparence des sprites");
        }
    }
}
