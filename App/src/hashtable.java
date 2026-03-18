import java.util.*;

public class hashtable {

    // Simulated video data
    static class VideoData {
        String videoId;
        String content;

        VideoData(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
        }
    }

    // Cache statistics
    static class CacheStats {
        int hits = 0;
        int misses = 0;
        long totalTime = 0;

        void recordHit(long time) {
            hits++;
            totalTime += time;
        }

        void recordMiss(long time) {
            misses++;
            totalTime += time;
        }

        double hitRate() {
            int total = hits + misses;
            return total == 0 ? 0 : (hits * 100.0 / total);
        }

        double avgTime() {
            int total = hits + misses;
            return total == 0 ? 0 : (totalTime * 1.0 / total);
        }
    }

    private final int L1_CAPACITY = 10000;
    private final int L2_CAPACITY = 100000;

    // L1: in-memory cache with LRU
    private LinkedHashMap<String, VideoData> l1Cache;
    private CacheStats l1Stats = new CacheStats();

    // L2: SSD-backed simulation
    private LinkedHashMap<String, VideoData> l2Cache;
    private CacheStats l2Stats = new CacheStats();
    private Map<String, Integer> l2AccessCount = new HashMap<>();

    // L3: database simulation
    private Map<String, VideoData> l3Database = new HashMap<>();
    private CacheStats l3Stats = new CacheStats();

    private final int PROMOTION_THRESHOLD = 3;

    public hashtable() {
        // Access-order LinkedHashMap for LRU
        l1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        l2Cache = new LinkedHashMap<>(L2_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L2_CAPACITY;
            }
        };

        // Preload L3 database
        for (int i = 1; i <= 1_000_000; i++) {
            String vid = "video_" + i;
            l3Database.put(vid, new VideoData(vid, "content_of_" + vid));
        }
    }

    // Get video
    public VideoData getVideo(String videoId) {
        long startTime = System.currentTimeMillis();

        // L1 lookup
        if (l1Cache.containsKey(videoId)) {
            long time = 0; // 0.5ms simulated
            l1Stats.recordHit(time);
            return l1Cache.get(videoId);
        } else {
            l1Stats.recordMiss(0); // L1 miss
        }

        // L2 lookup
        if (l2Cache.containsKey(videoId)) {
            long time = 5; // 5ms simulated
            l2Stats.recordHit(time);
            VideoData data = l2Cache.get(videoId);

            // Increment access count and maybe promote
            l2AccessCount.put(videoId, l2AccessCount.getOrDefault(videoId, 0) + 1);
            if (l2AccessCount.get(videoId) >= PROMOTION_THRESHOLD) {
                promoteToL1(videoId, data);
            }
            return data;
        } else {
            l2Stats.recordMiss(5);
        }

        // L3 lookup
        long time = 150; // 150ms simulated
        l3Stats.recordHit(time);
        VideoData data = l3Database.get(videoId);

        // Add to L2
        l2Cache.put(videoId, data);
        l2AccessCount.put(videoId, 1);

        return data;
    }

    private void promoteToL1(String videoId, VideoData data) {
        l1Cache.put(videoId, data);
        l2AccessCount.put(videoId, 0);
    }

    // Invalidate video
    public void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        l2AccessCount.remove(videoId);
        l3Database.remove(videoId);
    }

    // Cache statistics
    public void getStatistics() {
        double overallHits = l1Stats.hits + l2Stats.hits + l3Stats.hits;
        double overallTotal = overallHits + l1Stats.misses + l2Stats.misses + l3Stats.misses;

        System.out.println("L1: Hit Rate " + String.format("%.1f", l1Stats.hitRate()) + "%, Avg Time: " + String.format("%.1fms", l1Stats.avgTime()));
        System.out.println("L2: Hit Rate " + String.format("%.1f", l2Stats.hitRate()) + "%, Avg Time: " + String.format("%.1fms", l2Stats.avgTime()));
        System.out.println("L3: Hit Rate " + String.format("%.1f", l3Stats.hitRate()) + "%, Avg Time: " + String.format("%.1fms", l3Stats.avgTime()));
        System.out.println("Overall: Hit Rate " + String.format("%.1f", (overallHits * 100.0 / overallTotal)) + "%");
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {
        hashtable cache = new hashtable();

        // First request
        cache.getVideo("video_123");
        // Second request
        cache.getVideo("video_123");
        // New video not in L1 or L2
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}