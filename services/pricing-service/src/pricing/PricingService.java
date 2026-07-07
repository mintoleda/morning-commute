package pricing;

import events.TripCancelled;
import events.TripCompleted;
import events.TripRequested;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@KafkaListener(topics = "trips.events", groupId = "pricing-group")
public class PricingService {

    // track active trips for pricing
    private final Map<String, Integer> zoneDemand = new ConcurrentHashMap<>();

    @KafkaHandler
    public void consumeTripRequested(TripRequested event) {
        // increment active trips
        if ("TripRequested".equals(event.getEventType())) {
            zoneDemand.merge(event.getPickupZone(), 1, Integer::sum);
        }
    }

    @KafkaHandler
    public void consumeTripCompleted(TripCompleted event) {
        // decrement active trips
        if ("TripCompleted".equals(event.getEventType())) {
            zoneDemand.computeIfPresent(event.getPickupZone(), (k, v) -> Math.max(0, v - 1));
        }
    }

    @KafkaHandler
    public void consumeTripCancelled(TripCancelled event) {
        // same as completed
        if ("TripCancelled".equals(event.getEventType())) {
            zoneDemand.computeIfPresent(event.getPickupZone(), (k, v) -> Math.max(0, v - 1));
        }
    }

    public int getActiveTrips(String zoneId) {
        return zoneDemand.getOrDefault(zoneId, 0);
    }

    public double getMultiplier(String zoneId) {
        // multiplier = 1.0 + (active_trips / 10)
        int activeTrips = getActiveTrips(zoneId);
        return 1.0 + (activeTrips / 10.0);
    }
}
