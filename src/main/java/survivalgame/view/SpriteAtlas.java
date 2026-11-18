package survivalgame.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;

/**
 * Charge les tuiles et personnages depuis l'atlas pixel-art du jeu.
 * @author BORNET Floryan, CARRARA Tim, VIDY Enzo
 */
final class SpriteAtlas {
    /** Types de textures pouvant être découpées depuis l’atlas. */
    enum Sprite { FLOOR, BRICK, WINDOW, SANDBAG, PLAYER, ZOMBIE, FAST_ZOMBIE, BULLET }

    private final Map<Sprite, BufferedImage> sprites = new EnumMap<>(Sprite.class);

    /** Initialise l’atlas en chargeant puis découpant l’image globale. */
    SpriteAtlas() {
        try (InputStream input = SpriteAtlas.class.getResourceAsStream("/images/arena-atlas.png")) {
            if (input == null) throw new IOException("images/arena-atlas.png introuvable");
            BufferedImage atlas = ImageIO.read(input);
            if (atlas == null) throw new IOException("Format d'image non reconnu");
            extractSprites(atlas);
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de charger les textures du jeu.", exception);
        }
    }

    /** Retourne l’image associée à un type de sprite.
     * @param sprite type demandé
     * @return image découpée correspondante
     */
    BufferedImage get(Sprite sprite) {
        return sprites.get(sprite);
    }

    /**
     * Découpe l'atlas chargé afin d'associer chaque sprite à son image individuelle.
     * @param atlas image globale contenant toutes les textures du jeu
     */
    private void extractSprites(BufferedImage atlas) {
        for (Sprite sprite : Sprite.values()) {
            int[] bounds = switch (sprite) {
                case FLOOR -> new int[]{65, 70, 330, 325};
                case BRICK -> new int[]{500, 70, 335, 325};
                case WINDOW -> new int[]{935, 68, 350, 340};
                case SANDBAG -> new int[]{1375, 175, 355, 165};
                case PLAYER -> new int[]{80, 535, 325, 265};
                case ZOMBIE -> new int[]{530, 530, 285, 265};
                case FAST_ZOMBIE -> new int[]{940, 525, 335, 280};
                case BULLET -> new int[]{1500, 610, 100, 90};
            };
            sprites.put(sprite, atlas.getSubimage(bounds[0], bounds[1], bounds[2], bounds[3]));
        }
    }
}
