-- rides_db is created by POSTGRES_DB env var in docker-compose


CREATE TABLE IF NOT EXISTS trips (
    trip_id VARCHAR(255) PRIMARY KEY,
    rider_id VARCHAR(255),
    pickup_zone VARCHAR(50),
    dropoff_zone VARCHAR(50),
    status VARCHAR(50),
    requested_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE
);


CREATE TABLE IF NOT EXISTS analytics_zone_hourly (
    zone_id VARCHAR(50),
    hour_bucket TIMESTAMP,
    rides_requested BIGINT,
    rides_completed BIGINT,
    PRIMARY KEY (zone_id, hour_bucket)
);
