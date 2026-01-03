package analytics;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_zone_hourly")
@IdClass(ZoneAnalyticsId.class)
public class ZoneAnalytics {
    @Id
    private String zoneId;
    @Id
    private LocalDateTime hourBucket;

    private long ridesRequested;
    private long ridesCompleted;

    public ZoneAnalytics() {}

    public ZoneAnalytics(String zoneId, LocalDateTime hourBucket, long ridesRequested, long ridesCompleted) {
        this.zoneId = zoneId;
        this.hourBucket = hourBucket;
        this.ridesRequested = ridesRequested;
        this.ridesCompleted = ridesCompleted;
    }

    // standard getters and setters
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    public LocalDateTime getHourBucket() { return hourBucket; }
    public void setHourBucket(LocalDateTime hourBucket) { this.hourBucket = hourBucket; }
    public long getRidesRequested() { return ridesRequested; }
    public void setRidesRequested(long ridesRequested) { this.ridesRequested = ridesRequested; }
    public long getRidesCompleted() { return ridesCompleted; }
    public void setRidesCompleted(long ridesCompleted) { this.ridesCompleted = ridesCompleted; }
}

class ZoneAnalyticsId implements Serializable {
    private String zoneId;
    private LocalDateTime hourBucket;
    
    // required for composite key
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneAnalyticsId that = (ZoneAnalyticsId) o;
        return java.util.Objects.equals(zoneId, that.zoneId) &&
               java.util.Objects.equals(hourBucket, that.hourBucket);
    }
    public int hashCode() {
        return java.util.Objects.hash(zoneId, hourBucket);
    }
}
