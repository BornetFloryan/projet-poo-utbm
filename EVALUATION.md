# Vérification des critères d'évaluation

Cette grille reprend les critères du document `PR70_Modalités_Deval_26.pdf` fourni avec le projet et indique où les retrouver dans le code.

| Critère | Éléments présents | Vérification / limite |
|---|---|---|
| Interface graphique | Interface Swing avec menu, aide, classement, jeu, boutique, pause et fin de partie ; sprites et cartes dessinés dans `view/`. | Les commandes sont détaillées dans l'écran Aide. Vérifier les clics et le redimensionnement sur un poste graphique. |
| Gestion des fichiers | `ScoreManager` lit et écrit `highscores.txt`. `GameSaveManager` écrit et charge la partie dans `savegame.properties`. | Les erreurs d'accès et les données invalides sont remontées à l'interface avec un message. L'écriture de sauvegarde utilise un fichier temporaire et un remplacement atomique quand le système le permet. |
| Documentation | Documentation d'utilisation et d'architecture dans `README.md`, `ARCHITECTURE.md`, `TESTS.md` et `SOUTENANCE.md`, plus le diagramme `UML.puml`. Les types et API publiques Java sont documentés en Javadoc. | Générer la Javadoc avec la commande donnée dans `TESTS.md`. |
| Encapsulation, héritage, polymorphisme, modularité | Attributs d'état privés ; hiérarchie `Entity` → `Character` → `Player`/`Enemy` → zombies ; traitement commun des entités et packages séparés `model`, `view`, `controller`, `input`, `persistence`. | Voir `ARCHITECTURE.md` et `UML.puml`. |
| Qualité et lisibilité | Classes séparées par responsabilité, noms explicites, indentation Java, génération de carte factorisée, validations centralisées pour la sauvegarde. | Compilation avec les avertissements activés et vérifications de modèle décrites dans `TESTS.md`. |
| Fonctionnalité et robustesse | Progression en vagues, obstacles destructibles, boutique, tirs, collisions, score, chargement et reprise d'une sauvegarde complète ; formats et valeurs persistés validés. | Les cas modèle et rendu sont vérifiés automatiquement ; l'interface Swing doit également être parcourue manuellement. |
| Ergonomie et messages | Écrans distincts, commandes clavier/souris, adaptation à la taille de fenêtre, messages temporaires pour sauvegarde, chargement, achats et erreurs. | Une sauvegarde se crée depuis Pause → Sauvegarder ; Menu → Continuer la recharge. Le bouton Continuer affiche un message si aucun fichier n'existe. |

## Fichiers de données

`savegame.properties` est créé dans le dossier courant du processus au premier enregistrement. Il contient la graine de carte, la vague, les apparitions restantes, l'état du joueur, l'arme, les barricades, les ennemis et les projectiles. `highscores.txt` est indépendant et ne contient que les scores. Ces fichiers sont des données locales générées à l'exécution et ne doivent pas être supprimés par le nettoyage du dépôt.
