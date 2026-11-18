# Préparation à la soutenance

## Présentation

Survival Game 2D est un jeu d'arène en vue de dessus. Le joueur esquive les zombies, tire à la souris, collecte score et crédits, puis dépense ces crédits entre les vagues. Les zombies arrivent par des fenêtres barricadées et doivent détruire les planches pour entrer. Une échelle ajoute un point d'apparition à l'intérieur. La carte change toutes les trois vagues et les sprites mobiles pivotent dans leur direction. Java Swing gère les écrans et Java2D dessine le jeu à partir d'un atlas de sprites.

## Fonctionnalités présentes

Menu, instructions, classement, déplacement AZERTY/QWERTY, tir automatique, deux ennemis, collisions, invulnérabilité temporaire, vagues, boutique (deux armes, dégâts, cadence, soin), carte à génération pseudo-aléatoire, redimensionnement avec conservation des proportions, pause, game over, sauvegarde de progression et sauvegarde des scores.

## Choix POO à illustrer

- **Encapsulation** : `ScoreManager` garde son chemin de fichier privé et contrôle l'ajout via `addScore`.
- **Héritage** : `FastZombie` et `NormalZombie` héritent de `Enemy`, lui-même personnage et entité.
- **Polymorphisme** : la collection d'ennemis est déclarée `List<Enemy>` ; le rendu les distingue par type et chaque zombie porte ses propres paramètres de vie/vitesse hérités.
- **Composition** : une partie (`GameState`) conserve les objets qui participent à son état et sa carte.
- **MVC** : `model` contient les données/règles métier, `GameEngine` du package `controller` coordonne la simulation, `view` dessine l'état, et `persistence` gère les scores.

## Algorithmes à expliquer

Le vecteur de déplacement est normalisé pour que la diagonale ne soit pas plus rapide. Un BFS parcourt la grille à partir du joueur et fournit aux ennemis un champ de distance ; ceux-ci choisissent une case voisine plus proche. Le générateur vérifie ensuite que toutes les cases libres appartiennent au même ensemble accessible. Les coordonnées de simulation restent logiques ; seule la transformation graphique change quand la fenêtre est redimensionnée.

## Limites réellement constatées

Le diagramme UML fourni était une image ; le PlantUML a été réorganisé pour refléter les packages MVC et les classes du code. Les vérifications automatisées couvrent les règles et la persistance, mais pas les clics Swing ; la démonstration visuelle avec souris/clavier doit être faite sur un poste graphique.

## Améliorations possibles

Ajouter des effets sonores et animations, des tests de rendu en interface, une génération plus variée et un écran de saisie de pseudonyme intégré au lieu d'une boîte de dialogue. La sauvegarde reprend la vague à son début et ne conserve pas les ennemis ou projectiles déjà présents.

## Questions possibles

**Pourquoi normaliser les diagonales ?** La somme des composantes d'un déplacement diagonal serait plus grande que celle d'un déplacement horizontal ; la multiplication par `1/√2` conserve la même longueur.

**Comment le zombie contourne-t-il un mur ?** Le BFS remplit les distances depuis le joueur sur les cases accessibles ; le zombie choisit l'un des voisins dont la distance baisse.

**Pourquoi séparer score et crédits ?** Le score mesure la performance et reste cumulatif ; les crédits sont une ressource que les achats diminuent.

**Pourquoi une graine ?** `Random(seed)` reproduit les mêmes choix de murs, ce qui permet de retrouver et déboguer une carte.

**Comment éviter les dégâts répétés à chaque image ?** Le joueur mémorise l'heure du dernier coup et n'accepte un autre coup qu'après le délai d'invulnérabilité.

**Que se passe-t-il si le fichier de scores est mal formé ?** Les lignes invalides sont ignorées avec un message ; une erreur de fichier est remontée à l'écran, sans interrompre la partie.
