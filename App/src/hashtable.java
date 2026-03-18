import java.util.*;

public class hashtable {

    // Trie node class
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
        String word = null;
        int frequency = 0;
    }

    private TrieNode root;

    public hashtable() {
        root = new TrieNode();
    }

    // Add query to trie
    public void addQuery(String query, int freq) {
        TrieNode node = root;
        for (char ch : query.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.isWord = true;
        node.word = query;
        node.frequency += freq;
    }

    // Update frequency of an existing query (or add new)
    public void updateFrequency(String query) {
        addQuery(query, 1);
    }

    // Get top K suggestions for prefix
    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) return new ArrayList<>();
            node = node.children.get(ch);
        }

        PriorityQueue<TrieNode> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.frequency, b.frequency)
        );

        dfs(node, pq, 10);

        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(0, pq.poll().word); // reverse order for highest frequency first
        }

        return result;
    }

    // DFS to collect words under this node
    private void dfs(TrieNode node, PriorityQueue<TrieNode> pq, int k) {
        if (node.isWord) {
            pq.offer(node);
            if (pq.size() > k) pq.poll();
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, pq, k);
        }
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {
        hashtable autocomplete = new hashtable();

        // Preload some queries
        autocomplete.addQuery("java tutorial", 1234567);
        autocomplete.addQuery("javascript", 987654);
        autocomplete.addQuery("java download", 456789);
        autocomplete.addQuery("java 21 features", 200000);

        System.out.println("Search results for prefix 'jav':");
        List<String> suggestions = autocomplete.search("jav");
        int rank = 1;
        for (String s : suggestions) {
            System.out.println(rank + ". " + s);
            rank++;
        }

        // Update frequency
        autocomplete.updateFrequency("java 21 features");
        autocomplete.updateFrequency("java 21 features");

        System.out.println("\nAfter updating frequency for 'java 21 features':");
        suggestions = autocomplete.search("jav");
        rank = 1;
        for (String s : suggestions) {
            System.out.println(rank + ". " + s);
            rank++;
        }
    }
}