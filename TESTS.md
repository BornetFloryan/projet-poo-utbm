# Vérifications

Les règles principales peuvent être vérifiées sans framework. Depuis `Projet/` :

```sh
mkdir -p out
find src/main/java -name '*.java' -print > out/sources.txt
javac --release 17 -encoding UTF-8 -d out @out/sources.txt
cp -R src/main/resources/. out/
javac --release 17 -encoding UTF-8 -cp out -d out src/test/java/survivalgame/ModelChecks.java
javac --release 17 -encoding UTF-8 -cp out -d out src/test/java/survivalgame/view/RendererChecks.java
tmpdir=$(mktemp -d)
(cd "$tmpdir" && java -cp "$OLDPWD/out" survivalgame.ModelChecks && java -Djava.awt.headless=true -cp "$OLDPWD/out" survivalgame.view.RendererChecks)
```

Le test couvre 40 graines et leur connectivité, les fenêtres barricadées et l'échelle, le changement d'arène à la vague 3 avec conservation de la progression, les refus/acceptations et effets d'achats, la limite de soin, le polymorphisme, l'orientation du joueur et du zombie, le tir et ses récompenses, le classement corrompu, la sauvegarde/reprise de progression et l'atlas transparent. Les fichiers de test sont écrits dans un répertoire temporaire. Compiler et exécuter les vérifications après la modification de la sauvegarde.

La vérification manuelle sur poste avec environnement graphique reste nécessaire pour essayer visuellement le menu, le déplacement au clavier, la visée/tir, les transitions boutique/pause/game over et le redimensionnement. Il n'y a pas de test automatisé de clic Swing.
