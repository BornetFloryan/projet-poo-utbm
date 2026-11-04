package survivalgame.view;

import survivalgame.model.GameState;
import survivalgame.model.GameStatus;
import survivalgame.model.MapGenerator;
import survivalgame.model.FastZombie;
import survivalgame.model.Projectile;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

/** Vérifie le chargement des sprites et le rendu de chaque écran sans fenêtre native. */
public final class RendererChecks {
    public static void main(String[] args) {
        GameRenderer renderer = new GameRenderer();
        GameState state = new GameState(new MapGenerator().generate(77));
        state.getPlayer().setFacingAngle(Math.PI / 2);
        FastZombie zombie = new FastZombie(120, 120);
        zombie.setFacingAngle(Math.PI);
        state.getEnemies().add(zombie);
        state.getProjectiles().add(new Projectile(160, 120, 0, 8, 10));
        BufferedImage canvas = new BufferedImage(GameRenderer.WIDTH, GameRenderer.HEIGHT,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        for (GameStatus status : GameStatus.values()) {
            renderer.draw(graphics, status, state, List.of(), null, "");
        }
        graphics.dispose();
        if (canvas.getRGB(GameRenderer.WIDTH / 2, GameRenderer.HEIGHT / 2) == 0) {
            throw new AssertionError("Le rendu des écrans n'a produit aucun pixel.");
        }
        System.out.println("Rendu hors écran vérifié pour " + GameStatus.values().length + " états.");
    }
}
