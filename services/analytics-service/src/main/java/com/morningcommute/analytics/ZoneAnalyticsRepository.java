package com.morningcommute.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// data access for zone analytics
public interface ZoneAnalyticsRepository extends JpaRepository<ZoneAnalytics, ZoneAnalyticsId> {
}
