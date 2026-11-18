# Correspondance avec les critères d'évaluation

| Critère | Éléments du projet | Vérification / limite connue |
| --- | --- | --- |
| Interface graphique | Écrans Swing : menu, commandes, jeu, pause, boutique, scores et fin de partie ; sprites, boutons et HUD. | Les vérifications de rendu tournent sans fenêtre. Faire aussi une démonstration au clavier et à la souris sur un poste graphique. |
| Gestion de fichiers | `ScoreManager` lit et écrit `highscores.txt`. `GameSaveManager` lit et écrit `savegame.properties`. Les formats invalides et erreurs d'entrée-sortie sont gérés. | La progression sauvegarde la vague, la carte (graine), la vie, le score, les crédits et l'arme. Continuer reprend au début de la vague ; ennemis et projectiles présents ne sont pas conservés. |
| Documentation | README, architecture, UML PlantUML, préparation de soutenance et cette correspondance. Les classes principales comportent des commentaires Javadoc. | Compléter les commentaires Javadoc de chaque méthode publique si l'enseignant interprète « tous les éléments » au sens strict. |
| Principes POO | Encapsulation, héritage (`Entity` → `Character` → `Player`/`Enemy` → types de zombies), polymorphisme des ennemis, composition et packages MVC. | Les tests vérifient les règles et les types d'ennemis. |
| Qualité et lisibilité | Packages par responsabilité, fichier par classe, noms explicites, indentation cohérente, pas de dépendance externe. | Revue visuelle complémentaire recommandée avant la soutenance. |
| Fonctionnalité et robustesse | Déplacement, collisions, tirs, ennemis, barricades, vagues, boutique, génération de carte et persistance. | Les vérifications automatisées couvrent le modèle et le rendu hors écran ; les interactions Swing demandent une vérification manuelle. |
| Ergonomie | Commandes affichées, pause, libellés des actions et retours d'erreur dans l'interface. | Tester la lisibilité des libellés et le redimensionnement sur la machine de présentation. |

## Fichiers créés à l'exécution

- `highscores.txt` apparaît lors du premier enregistrement d'un score.
- `savegame.properties` apparaît lors de la première sauvegarde depuis l'écran de pause.

Ils sont placés dans le dossier courant depuis lequel le jeu est lancé.
