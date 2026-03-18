import java.util.*;

public class hashtable {

    // n-gram -> set of document IDs
    private Map<String, Set<String>> ngramIndex;

    // document -> its n-grams
    private Map<String, List<String>> documentNgrams;

    private int N = 5; // 5-gram

    public hashtable() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Add document to system
    public void addDocument(String docId, String text) {
        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId, String text) {
        List<String> ngrams = generateNgrams(text);

        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDoc : ngramIndex.get(gram)) {
                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Analyzing: " + docId);
        System.out.println("Total n-grams: " + ngrams.size());

        // Find similarity
        for (String existingDoc : matchCount.keySet()) {
            int matches = matchCount.get(existingDoc);
            int total = ngrams.size();

            double similarity = (matches * 100.0) / total;

            System.out.println("Matched with " + existingDoc +
                    " → " + matches + " n-grams → Similarity: " +
                    String.format("%.2f", similarity) + "%");
        }

        // Find most similar
        String bestMatch = null;
        int maxMatch = 0;

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            if (entry.getValue() > maxMatch) {
                maxMatch = entry.getValue();
                bestMatch = entry.getKey();
            }
        }

        if (bestMatch != null) {
            double similarity = (maxMatch * 100.0) / ngrams.size();

            System.out.println("Most similar: " + bestMatch +
                    " → " + String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            }
        }
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {
        List<String> result = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            result.add(gram.toString().trim());
        }

        return result;
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {

        hashtable detector = new hashtable();

        // Existing documents
        detector.addDocument("essay_089",
                "machine learning is a subset of artificial intelligence and data science");

        detector.addDocument("essay_092",
                "machine learning is a subset of artificial intelligence and data science widely used today");

        // New document
        detector.analyzeDocument("essay_123",
                "machine learning is a subset of artificial intelligence and data science");
    }
}