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
javadoc -quiet -public -Xdoclint:all -d /tmp/projet-javadocs @out/sources.txt
```

Le test couvre 40 graines et leur connectivité, les fenêtres barricadées et l'échelle, le changement d'arène à la vague 3 avec conservation de la progression, les achats, le polymorphisme, l'orientation, les tirs et récompenses, le classement corrompu, la restauration complète d'une partie et l'atlas transparent. Les fichiers de test sont écrits dans un répertoire temporaire. La compilation Java 17 réussit et le modèle a été vérifié avec 171 assertions.

La vérification manuelle sur poste avec environnement graphique reste nécessaire pour essayer le menu, les boutons de sauvegarde et de reprise, le déplacement, la visée/tir, les transitions boutique/pause/game over et le redimensionnement. Il n'y a pas de test automatisé de clic Swing.
