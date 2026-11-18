# Survival Game 2D — Édition Arène

Jeu de survie 2D en Java Swing, Java2D et Java 17+. Le dépôt ne nécessite aucune bibliothèque externe. Les sprites pixel-art sont regroupés dans un atlas inclus au projet.

## Prérequis et lancement

Il faut un JDK 17 ou supérieur et un environnement graphique (le jeu utilise Swing).

Depuis le dossier `Projet/` :

```sh
./run.sh
```

Le script compile avec `javac` dans `out/` puis lance `survivalgame.Main`. En IDE, ouvrir `Projet/` et sélectionner `src/main/java` comme racine source. Le classement est enregistré dans `highscores.txt`. Depuis l'écran de pause, « Sauvegarder » crée `savegame.properties` dans le dossier courant ; « Continuer » au menu charge cette partie.

## Commandes

- `ZQSD` ou `WASD` : déplacement (diagonales normalisées).
- Souris : visée ; maintenir le clic gauche : tir automatique.
- `Échap` : pause/reprise.
- Écran de pause : sauvegarder la partie en cours.
- Entre les vagues : achats dans la boutique.

## Organisation

- `src/main/java/survivalgame/Main.java` : lancement.
- `model/` : classes du domaine, une classe par fichier.
- `controller/GameEngine.java` : mises à jour, collisions et transitions de jeu.
- `input/` : saisie clavier et souris.
- `view/` : fenêtre, panneau Swing, rendu et chargement des sprites.
- `persistence/` : lecture/écriture du classement et des parties.
- `src/main/resources/images/arena-atlas.png` : textures du terrain et sprites.
- `ARCHITECTURE.md` et `SOUTENANCE.md` : architecture et présentation du projet.
- `UML.puml` : diagramme PlantUML à jour.

La carte fait 22 × 14 tuiles. Elle a quatre fenêtres barricadées et une échelle intérieure dont les positions varient d'une carte à l'autre. Une nouvelle carte apparaît toutes les trois vagues. Les zombies essaient de passer par les fenêtres et attaquent les barricades lorsqu'elles bloquent leur chemin ; le joueur peut aussi les détruire. Les sprites mobiles pivotent vers leur direction de visée ou de déplacement.

Le diagramme PlantUML reflète les classes et les packages du projet et peut être importé dans draw.io.
