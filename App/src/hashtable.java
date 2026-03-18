import java.util.*;
import java.util.concurrent.*;

public class hashtable {

    // productId -> stock
    private ConcurrentHashMap<String, Integer> stockMap;

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public hashtable() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product with stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    // Check stock (O(1))
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe, prevents overselling)
    public String purchaseItem(String productId, int userId) {

        // Lock per product
        synchronized (productId.intern()) {

            int stock = stockMap.getOrDefault(productId, 0);

            if (stock > 0) {
                stockMap.put(productId, stock - 1);
                return "Success, remaining stock: " + (stock - 1);
            } else {
                Queue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                return "Added to waiting list, position #" + queue.size();
            }
        }
    }

    // Get waiting list
    public Queue<Integer> getWaitingList(String productId) {
        return waitingList.get(productId);
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {

        hashtable manager = new hashtable();

        manager.addProduct("IPHONE15_256GB", 3);

        System.out.println(manager.checkStock("IPHONE15_256GB"));
        // → 3

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 101));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 102));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 103));

        // Stock खत्म (out of stock)
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 104));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 105));

        // Waiting list
        System.out.println(manager.getWaitingList("IPHONE15_256GB"));
    }
}