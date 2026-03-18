import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class hashtable {

    // TokenBucket class
    static class TokenBucket {
        private final int maxTokens;
        private final int refillRatePerHour;
        private AtomicInteger tokens;
        private long lastRefillTime;

        public TokenBucket(int maxTokens, int refillRatePerHour) {
            this.maxTokens = maxTokens;
            this.refillRatePerHour = refillRatePerHour;
            this.tokens = new AtomicInteger(maxTokens);
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean allowRequest() {
            refillTokens();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            } else {
                return false;
            }
        }

        private void refillTokens() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;

            // Refill every hour
            if (elapsed >= 3600_000) {
                tokens.set(maxTokens);
                lastRefillTime = now;
            }
        }

        public int remainingTokens() {
            refillTokens();
            return tokens.get();
        }

        public long getResetTime() {
            refillTokens();
            return lastRefillTime + 3600_000;
        }
    }

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clients;

    private final int MAX_REQUESTS = 1000;

    public hashtable() {
        clients = new ConcurrentHashMap<>();
    }

    // Check rate limit for client
    public String checkRateLimit(String clientId) {
        clients.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, MAX_REQUESTS));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.remainingTokens() + " requests remaining)";
        } else {
            long retryAfter = (bucket.getResetTime() - System.currentTimeMillis()) / 1000;
            return "Denied (0 requests remaining, retry after " + retryAfter + "s)";
        }
    }

    // Get current status
    public Map<String, Object> getRateLimitStatus(String clientId) {
        clients.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, MAX_REQUESTS));
        TokenBucket bucket = clients.get(clientId);

        Map<String, Object> status = new HashMap<>();
        status.put("used", MAX_REQUESTS - bucket.remainingTokens());
        status.put("limit", MAX_REQUESTS);
        status.put("reset", bucket.getResetTime() / 1000); // epoch seconds
        return status;
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) throws InterruptedException {
        hashtable rateLimiter = new hashtable();

        String client = "abc123";

        // Simulate a few requests
        System.out.println(rateLimiter.checkRateLimit(client));
        System.out.println(rateLimiter.checkRateLimit(client));
        System.out.println(rateLimiter.checkRateLimit(client));

        // Show status
        System.out.println(rateLimiter.getRateLimitStatus(client));
    }
}