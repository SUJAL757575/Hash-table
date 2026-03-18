import java.util.*;

public class hashtable {

    // DNS Entry class
    class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;
    private int capacity;

    // Stats
    private int hits = 0;
    private int misses = 0;

    public hashtable(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > hashtable.this.capacity;
            }
        };

        // Start cleanup thread
        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {
        long start = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }

        // Cache miss → simulate upstream DNS
        misses++;
        String newIP = queryUpstreamDNS(domain);

        // Store with TTL (example: 5 sec)
        cache.put(domain, new DNSEntry(domain, newIP, 5));

        return "Cache MISS → " + newIP;
    }

    // Simulated DNS lookup
    private String queryUpstreamDNS(String domain) {
        // Dummy IP generator
        return "192.168.1." + (new Random().nextInt(200) + 1);
    }

    // Cache stats
    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + hitRate + "% (Hits: " + hits + ", Misses: " + misses + ")";
    }

    // Cleanup expired entries periodically
    private void startCleanupThread() {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000); // run every 2 sec

                    synchronized (this) {
                        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry<String, DNSEntry> entry = it.next();
                            if (entry.getValue().isExpired()) {
                                it.remove();
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) throws InterruptedException {

        hashtable dnsCache = new hashtable(3);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com")); // HIT

        Thread.sleep(6000); // wait for TTL expiry

        System.out.println(dnsCache.resolve("google.com")); // EXPIRED → MISS

        System.out.println(dnsCache.getCacheStats());
    }
}