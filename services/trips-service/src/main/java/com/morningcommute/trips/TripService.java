package com.morningcommute.trips;

import com.morningcommute.events.TripRequested;
import com.morningcommute.events.TripCompleted;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

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
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("Trip with id " + tripId + " not found"));
        if (trip.getStatus() == Trip.Status.COMPLETE) {
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

    public List<Trip> getTripsByRider(String riderId) {
        return tripRepository.findByRiderID(riderId);
    }

    public Trip getTrip(String tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new TripNotFoundException("Trip with id " + tripId + " not found"));
    }
}
