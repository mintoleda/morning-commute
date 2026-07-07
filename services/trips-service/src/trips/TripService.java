package trips;

import events.TripCancelled;
import events.TripCompleted;
import events.TripRequested;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TripService(TripRepository tripRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.tripRepository = tripRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void createTrip(Trip trip) {
        // generate trip id if missing
        if (trip.getTripID() == null) {
            trip.setTripID(UUID.randomUUID().toString());
        }
        trip.setStatus(Trip.Status.REQUESTED);
        trip.setRequestedAt(java.time.OffsetDateTime.now());
        tripRepository.save(trip);

        // publish trip requested event
        TripRequested event = new TripRequested();

        event.setTripID(trip.getTripID());
        event.setRiderID(trip.getRiderID());
        event.setPickupZone(trip.getPickupZone());
        event.setDropoffZone(trip.getDropoffZone());
        event.setRequestedAt(trip.getRequestedAt());

        event.setEventType("TripRequested");
        event.setEventVersion(1);
        kafkaTemplate.send("trips.events", trip.getTripID(), event);
    }

    public void completeTrip(String tripId) {
        Trip trip =
                tripRepository
                        .findById(tripId)
                        .orElseThrow(
                                () ->
                                        new TripNotFoundException(
                                                "Trip with id " + tripId + " not found"));
        if (trip.getStatus() != Trip.Status.REQUESTED) {
            return;
        }

        trip.setStatus(Trip.Status.COMPLETE);
        trip.setCompletedAt(java.time.OffsetDateTime.now());
        tripRepository.save(trip);

        TripCompleted event = new TripCompleted();
        event.setEventType("TripCompleted");
        event.setEventVersion(1);
        event.setTripID(trip.getTripID());
        event.setRiderID(trip.getRiderID());
        event.setPickupZone(trip.getPickupZone());
        event.setDropoffZone(trip.getDropoffZone());
        event.setRequestedAt(trip.getRequestedAt());
        event.setCompletedAt(trip.getCompletedAt());

        kafkaTemplate.send("trips.events", trip.getTripID(), event);
    }

    public void cancelTrip(String tripId) {
        Trip trip =
                tripRepository
                        .findById(tripId)
                        .orElseThrow(
                                () ->
                                        new TripNotFoundException(
                                                "Trip with id " + tripId + " not found"));
        if (trip.getStatus() != Trip.Status.REQUESTED) {
            return;
        }

        trip.setStatus(Trip.Status.CANCELLED);
        trip.setCancelledAt(java.time.OffsetDateTime.now());
        tripRepository.save(trip);

        TripCancelled event = new TripCancelled();
        event.setEventType("TripCancelled");
        event.setEventVersion(1);
        event.setTripID(trip.getTripID());
        event.setRiderID(trip.getRiderID());
        event.setPickupZone(trip.getPickupZone());
        event.setDropoffZone(trip.getDropoffZone());
        event.setRequestedAt(trip.getRequestedAt());
        event.setCancelledAt(trip.getCancelledAt());

        kafkaTemplate.send("trips.events", trip.getTripID(), event);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<Trip> getTripsByRider(String riderId) {
        return tripRepository.findByRiderID(riderId);
    }

    public Trip getTrip(String tripId) {
        return tripRepository
                .findById(tripId)
                .orElseThrow(
                        () -> new TripNotFoundException("Trip with id " + tripId + " not found"));
    }
}
