import java.util.*;

public class hashtable {

    // username -> userId
    private Map<String, Integer> usernameMap;

    // username -> attempt count
    private Map<String, Integer> attemptCount;

    public hashtable() {
        usernameMap = new HashMap<>();
        attemptCount = new HashMap<>();
    }

    // Register a username
    public boolean register(String username, int userId) {
        username = username.toLowerCase();

        if (usernameMap.containsKey(username)) {
            return false;
        }

        usernameMap.put(username, userId);
        return true;
    }

    // Check availability (O(1))
    public boolean checkAvailability(String username) {
        username = username.toLowerCase();

        // Track attempts
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        username = username.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        // Add numbers
        for (int i = 1; i <= 5; i++) {
            String newName = username + i;
            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        // Replace "_" with "."
        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        // Prefix variations
        String[] prefixes = {"the", "real", "official"};
        for (String prefix : prefixes) {
            String newName = prefix + "_" + username;
            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        if (attemptCount.isEmpty()) return null;

        String maxUser = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attemptCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser;
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {
        hashtable checker = new hashtable();

        // Pre-existing users
        checker.register("john_doe", 101);
        checker.register("admin", 1);

        System.out.println(checker.checkAvailability("john_doe"));   // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        System.out.println(checker.suggestAlternatives("john_doe"));

        System.out.println(checker.getMostAttempted());
    }
}import java.util.*;

public class hashtable {

    // username -> userId
    private Map<String, Integer> usernameMap;

    // username -> attempt count
    private Map<String, Integer> attemptCount;

    public hashtable() {
        usernameMap = new HashMap<>();
        attemptCount = new HashMap<>();
    }

    // Register a username
    public boolean register(String username, int userId) {
        username = username.toLowerCase();

        if (usernameMap.containsKey(username)) {
            return false;
        }

        usernameMap.put(username, userId);
        return true;
    }

    // Check availability (O(1))
    public boolean checkAvailability(String username) {
        username = username.toLowerCase();

        // Track attempts
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        username = username.toLowerCase();
        List<String> suggestions = new ArrayList<>();

        // Add numbers
        for (int i = 1; i <= 5; i++) {
            String newName = username + i;
            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        // Replace "_" with "."
        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        // Prefix variations
        String[] prefixes = {"the", "real", "official"};
        for (String prefix : prefixes) {
            String newName = prefix + "_" + username;
            if (!usernameMap.containsKey(newName)) {
                suggestions.add(newName);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        if (attemptCount.isEmpty()) return null;

        String maxUser = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attemptCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser;
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {
        hashtable checker = new hashtable();

        // Pre-existing users
        checker.register("john_doe", 101);
        checker.register("admin", 1);

        System.out.println(checker.checkAvailability("john_doe"));   // false
        System.out.println(checker.checkAvailability("jane_smith")); // true

        System.out.println(checker.suggestAlternatives("john_doe"));

        System.out.println(checker.getMostAttempted());
    }
}