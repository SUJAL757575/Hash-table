import java.util.*;
import java.util.concurrent.*;

public class hashtable {

    // pageUrl -> total visit count
    private ConcurrentHashMap<String, Integer> pageViews;

    // pageUrl -> set of unique userIds
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors;

    // source -> count
    private ConcurrentHashMap<String, Integer> trafficSources;

    private ScheduledExecutorService scheduler;

    public hashtable() {
        pageViews = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        trafficSources = new ConcurrentHashMap<>();

        // Schedule dashboard updates every 5 seconds
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateDashboard, 5, 5, TimeUnit.SECONDS);
    }

    // Event object
    public static class PageViewEvent {
        String url;
        String userId;
        String source;

        public PageViewEvent(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    // Process incoming event
    public void processEvent(PageViewEvent event) {
        // Update total views
        pageViews.merge(event.url, 1, Integer::sum);

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic source
        trafficSources.merge(event.source.toLowerCase(), 1, Integer::sum);
    }

    // Update dashboard
    private void updateDashboard() {
        System.out.println("\n--- DASHBOARD UPDATE ---");

        // Top 10 pages by views
        PriorityQueue<Map.Entry<String, Integer>> topPagesPQ =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        topPagesPQ.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");
        int rank = 1;
        for (int i = 0; i < 10 && !topPagesPQ.isEmpty(); i++) {
            Map.Entry<String, Integer> entry = topPagesPQ.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int uniques = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            System.out.println(rank + ". " + url + " - " + views + " views (" + uniques + " unique)");
            rank++;
        }

        // Traffic sources percentage
        int totalTraffic = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = (entry.getValue() * 100.0) / totalTraffic;
            System.out.println(capitalize(entry.getKey()) + ": " + String.format("%.1f", percent) + "%");
        }
    }

    private String capitalize(String str) {
        if (str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Shutdown scheduler
    public void shutdown() {
        scheduler.shutdown();
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) throws InterruptedException {
        hashtable analytics = new hashtable();

        // Simulate page view events
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_123", "direct"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "google"));

        // Keep program alive for a few dashboard updates
        Thread.sleep(12000);

        analytics.shutdown();
    }
}