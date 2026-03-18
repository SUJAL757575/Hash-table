import java.util.*;

public class hashtable {

    // Spot status
    enum Status { EMPTY, OCCUPIED, DELETED }

    // Parking spot structure
    class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    private ParkingSpot[] spots;
    private int capacity;
    private int totalProbes = 0;
    private int parkedVehicles = 0;
    private Map<Integer, Integer> hourlyOccupancy; // Hour -> count

    public hashtable(int capacity) {
        this.capacity = capacity;
        spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) spots[i] = new ParkingSpot();
        hourlyOccupancy = new HashMap<>();
    }

    // Simple hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle
    public String parkVehicle(String licensePlate) {
        int preferred = hash(licensePlate);
        int probe = 0;

        while (probe < capacity) {
            int spot = (preferred + probe) % capacity;
            if (spots[spot].status == Status.EMPTY || spots[spot].status == Status.DELETED) {
                spots[spot].licensePlate = licensePlate;
                spots[spot].entryTime = System.currentTimeMillis();
                spots[spot].status = Status.OCCUPIED;

                totalProbes += probe;
                parkedVehicles++;

                // Track occupancy per hour
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                hourlyOccupancy.put(hour, hourlyOccupancy.getOrDefault(hour, 0) + 1);

                return "Assigned spot #" + spot + " (" + probe + " probes)";
            }
            probe++;
        }

        return "Parking Full!";
    }

    // Exit vehicle
    public String exitVehicle(String licensePlate) {
        int preferred = hash(licensePlate);
        int probe = 0;

        while (probe < capacity) {
            int spot = (preferred + probe) % capacity;
            if (spots[spot].status == Status.OCCUPIED && spots[spot].licensePlate.equals(licensePlate)) {
                long exitTime = System.currentTimeMillis();
                long durationMillis = exitTime - spots[spot].entryTime;
                double hours = durationMillis / 3600_000.0;
                double fee = Math.round(hours * 5 * 100.0) / 100.0; // $5 per hour

                spots[spot].status = Status.DELETED;
                parkedVehicles--;

                return "Spot #" + spot + " freed, Duration: " +
                        String.format("%.2f", hours) + "h, Fee: $" + fee;
            }
            probe++;
        }

        return "Vehicle not found!";
    }

    // Parking statistics
    public String getStatistics() {
        double occupancy = (parkedVehicles * 100.0) / capacity;
        double avgProbes = parkedVehicles == 0 ? 0 : (totalProbes * 1.0 / parkedVehicles);

        // Find peak hour
        int peakHour = -1;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : hourlyOccupancy.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                peakHour = entry.getKey();
            }
        }

        return "Occupancy: " + String.format("%.1f", occupancy) + "%, Avg Probes: " +
                String.format("%.2f", avgProbes) + ", Peak Hour: " + peakHour + ":00";
    }

    // ------------------- MAIN METHOD -------------------
    public static void main(String[] args) throws InterruptedException {
        hashtable parkingLot = new hashtable(500);

        System.out.println(parkingLot.parkVehicle("ABC-1234"));
        System.out.println(parkingLot.parkVehicle("ABC-1235"));
        System.out.println(parkingLot.parkVehicle("XYZ-9999"));

        Thread.sleep(5000); // simulate parking duration

        System.out.println(parkingLot.exitVehicle("ABC-1234"));
        System.out.println(parkingLot.getStatistics());
    }
}