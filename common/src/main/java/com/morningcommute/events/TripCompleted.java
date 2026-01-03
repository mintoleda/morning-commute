package com.morningcommute.events;

import java.time.OffsetDateTime;

// event payload for completed trips
public class TripCompleted {
    // event data
    private String eventType;
    private int eventVersion;

    // identifiers
    private String tripID;
    private String riderID;

    // location data
    private String pickupZone;
    private String dropoffZone;

    private OffsetDateTime requestedAt;
    private OffsetDateTime completedAt;

    public TripCompleted() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public void setEventVersion(int eventVersion) {
        this.eventVersion = eventVersion;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public String getPickupZone() {
        return pickupZone;
    }

    public void setPickupZone(String pickupZone) {
        this.pickupZone = pickupZone;
    }

    public String getDropoffZone() {
        return dropoffZone;
    }

    public void setDropoffZone(String dropoffZone) {
        this.dropoffZone = dropoffZone;
    }

    public OffsetDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(OffsetDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
