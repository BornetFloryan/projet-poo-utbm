# Architecture

Le code suit une organisation MVC simple, avec un fichier par classe et une responsabilité claire.

```text
survivalgame/
  Main.java
  model/        entités, carte, vagues, boutique et états
  controller/   GameEngine : règles et transitions
  input/        KeyboardInput et MouseInput
  view/         MainWindow, GamePanel, GameRenderer, SpriteAtlas
  persistence/  ScoreManager et ScoreEntry
resources/images/arena-atlas.png
```

## Classes principales et POO

- `Entity` porte position et dimensions ; `Character` ajoute vie, vitesse et rayon.
- `Player` et `Enemy` héritent de `Character`. `NormalZombie` et `FastZombie` héritent de `Enemy`. Les ennemis sont stockés en `List<Enemy>` et leur mise à jour polymorphe prend en compte la vitesse propre à chaque sous-type.
- `Projectile` hérite d'`Entity`. `Element` décrit le décor immobile et `Obstacle` les cases solides.
- `GameState` possède le joueur, les ennemis, les tirs et la carte. `GameEngine` pilote l'évolution des données. `GamePanel` affiche les états et transforme les coordonnées souris.
- `MapGenerator` construit une grille pseudo-aléatoire reproductible par graine ; `WaveManager` crée les ennemis ; `ShopManager` valide et applique les achats ; `ScoreManager` gère le fichier.
- L'encapsulation est visible dans les champs privés (par exemple le contrôle des entrées et le chemin de score) et les méthodes publiques limitées aux opérations nécessaires.

## Boucle, collisions et navigation

Un `javax.swing.Timer` déclenche la simulation environ toutes les 16 ms sur l'Event Dispatch Thread. La mise à jour n'attend aucune entrée : l'état des touches maintenues pilote le déplacement. Le vecteur diagonal est multiplié par `1/√2`. Le déplacement est résolu séparément sur X et Y ; les quatre coins du rayon testent les murs.

Les projectiles avancent selon un vecteur normalisé, perdent de la portée, et sont supprimés au contact d'un mur, à la sortie ou après avoir touché un ennemi. Le nombre simultané est plafonné. Un ennemi inflige un coup avec un délai d'invulnérabilité de 850 ms.

À chaque mise à jour, un parcours en largeur depuis la case du joueur calcule la distance des cases accessibles. Chaque zombie se dirige vers un voisin de distance plus faible, ce qui lui permet de contourner un obstacle. Pour cette carte de 22 × 14 cases, ce calcul reste léger et simple à expliquer.

## Carte, vagues et boutique

La surface logique reste 960 × 640, mais l'arène est plus petite (22 × 14 cases, soit 704 × 448 pixels) et centrée. Chaque carte place différemment quatre fenêtres d'entrée et une échelle intérieure. Les zombies apparaissent sur ces cinq points ; ils avancent vers le joueur et attaquent une barricade si elle bloque leur chemin. Une nouvelle carte est créée à chaque troisième vague, en garantissant des points d'apparition différents ; la vie, le score, les crédits et l'arme restent ceux du joueur. Les obstacles internes ajoutent des couverts et des détours. La graine pseudo-aléatoire permet de reproduire une carte ; un BFS contrôle que les zones libres restent reliées. Le redimensionnement conserve les proportions et les coordonnées logiques.

`GameRenderer` utilise l'atlas `arena-atlas.png` : sol fissuré, briques, fenêtres barricadées, sacs de sable, survivant, deux zombies et projectile. `SpriteAtlas` charge les images via les ressources Java ; le rendu les agrandit en conservant les pixels nets.

La première vague comprend 7 ennemis ; chaque vague ajoute 2 ennemis jusqu'au plafond de 32 et augmente progressivement la part de rapides. `WaveManager` les fait apparaître progressivement (environ un toutes les 430 ms), avec au plus 14 ennemis présents à la fois. Quand tous les ennemis de la vague ont été générés et éliminés, l'état boutique arrête la simulation. Le score et les crédits sont distincts. La boutique vérifie le solde, limite les bonus de dégâts/cadence et limite la vie au maximum.

## Fichiers

`ScoreManager` charge `highscores.txt` s'il existe, ignore une ligne corrompue avec un message sur la sortie d'erreur, trie les entrées et affiche les dix meilleures. Chaque enregistrement valide le pseudonyme avant l'ajout. `GameSaveManager` écrit la progression dans `savegame.properties` : graine de carte, vague, points de vie, score, crédits et arme. La carte est régénérée avec la même graine. La reprise repart au début de la vague sauvegardée ; les ennemis déjà présents et les projectiles ne sont pas conservés. Les erreurs de lecture ou d'écriture sont remontées à l'interface. Ces fichiers sont relatifs au dossier de lancement.
