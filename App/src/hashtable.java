import java.util.*;

public class hashtable {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long timestamp; // milliseconds since epoch

        public Transaction(int id, int amount, String merchant, String account, long timestamp) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.timestamp = timestamp;
        }
    }

    private List<Transaction> transactions;

    public hashtable() {
        transactions = new ArrayList<>();
    }

    // Add transaction
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }
        return result;
    }

    // Two-Sum with time window (milliseconds)
    public List<int[]> findTwoSumWithWindow(int target, long windowMillis) {
        Map<Integer, List<Transaction>> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction c : map.get(complement)) {
                    if (Math.abs(t.timestamp - c.timestamp) <= windowMillis) {
                        result.add(new int[]{c.id, t.id});
                    }
                }
            }
            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }

        return result;
    }

    // K-Sum (recursive)
    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        findKSumHelper(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void findKSumHelper(List<Transaction> trans, int k, int target, int start,
                                List<Integer> path, List<List<Integer>> result) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        if (k == 0) return;

        for (int i = start; i < trans.size(); i++) {
            path.add(trans.get(i).id);
            findKSumHelper(trans, k - 1, target - trans.get(i).amount, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    // Detect duplicates: same amount, same merchant, different accounts
    public List<Map<String, Object>> detectDuplicates() {
        Map<String, List<String>> map = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant;
            map.putIfAbsent(key, new ArrayList<>());
            if (!map.get(key).contains(t.account)) map.get(key).add(t.account);
        }

        for (Map.Entry<String, List<String>> e : map.entrySet()) {
            if (e.getValue().size() > 1) {
                String[] parts = e.getKey().split("_");
                Map<String, Object> dup = new HashMap<>();
                dup.put("amount", Integer.parseInt(parts[0]));
                dup.put("merchant", parts[1]);
                dup.put("accounts", e.getValue());
                result.add(dup);
            }
        }

        return result;
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) {
        hashtable ht = new hashtable();

        long baseTime = System.currentTimeMillis();

        ht.addTransaction(new Transaction(1, 500, "Store A", "acc1", baseTime));
        ht.addTransaction(new Transaction(2, 300, "Store B", "acc2", baseTime + 15*60*1000));
        ht.addTransaction(new Transaction(3, 200, "Store C", "acc3", baseTime + 30*60*1000));
        ht.addTransaction(new Transaction(4, 500, "Store A", "acc2", baseTime + 45*60*1000));

        System.out.println("Classic Two-Sum target=500:");
        for (int[] pair : ht.findTwoSum(500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nTwo-Sum within 1 hour:");
        for (int[] pair : ht.findTwoSumWithWindow(500, 3600_000)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nK-Sum (k=3, target=1000):");
        for (List<Integer> combo : ht.findKSum(3, 1000)) {
            System.out.println(combo);
        }

        System.out.println("\nDuplicate detection:");
        for (Map<String, Object> dup : ht.detectDuplicates()) {
            System.out.println(dup);
        }
    }
}