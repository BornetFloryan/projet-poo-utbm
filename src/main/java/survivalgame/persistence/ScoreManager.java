package survivalgame.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Lit et enregistre les meilleurs scores dans highscores.txt. */
public final class ScoreManager {
    private final Path file = Path.of("highscores.txt");

    public List<ScoreEntry> getTopScores() throws IOException {
        List<ScoreEntry> scores = loadScores();
        scores.sort(Comparator.comparingInt(ScoreEntry::score).reversed());
        return scores.subList(0, Math.min(10, scores.size()));
    }

    public void addScore(String name, int score) throws IOException {
        String cleanName = name == null ? "" : name.strip();
        if (cleanName.isEmpty() || cleanName.length() > 16
                || cleanName.contains("\t") || cleanName.contains("\n")) {
            throw new IllegalArgumentException("Pseudonyme invalide (1 à 16 caractères).");
        }
        Files.writeString(file, cleanName + "\t" + score + System.lineSeparator(),
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private List<ScoreEntry> loadScores() throws IOException {
        List<ScoreEntry> scores = new ArrayList<>();
        if (!Files.exists(file)) return scores;
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (line.isBlank()) continue;
            String[] fields = line.split("\\t", 2);
            try {
                if (fields.length != 2) throw new NumberFormatException();
                scores.add(new ScoreEntry(fields[0], Integer.parseInt(fields[1])));
            } catch (NumberFormatException exception) {
                System.err.println("Ligne de score ignorée (" + lineNumber + ").");
            }
        }
        return scores;
    }
}
