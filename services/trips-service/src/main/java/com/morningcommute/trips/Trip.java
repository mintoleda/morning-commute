package com.morningcommute.trips;

import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "trips")
// trip entity definition
public class Trip {
    @Id
    @Column(name = "trip_id")
    private String tripID;
    @Column(name = "rider_id")
    private String riderID;

    @Column(name = "pickup_zone")
    private String pickupZone;
    @Column(name = "dropoff_zone")
    private String dropoffZone;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    public Trip() {
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public enum Status {
        REQUESTED,
        COMPLETE
    };
}
