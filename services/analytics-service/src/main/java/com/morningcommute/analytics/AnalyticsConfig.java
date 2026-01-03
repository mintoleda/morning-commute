package com.morningcommute.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.common.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Configuration
public class AnalyticsConfig {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsConfig.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ZoneAnalyticsRepository repository;

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        // read from trips.events topic and aggregate by pickup zone and 1-hour window
        KStream<String, String> stream = builder.stream("trips.events", Consumed.with(Serdes.String(), Serdes.String()));

        stream.peek((key, value) -> log.info("Received event: {}", value))
              .mapValues(value -> {
                  try {
                      return objectMapper.readTree(value);
                  } catch (JsonProcessingException e) {
                      return null;
                  }
              })
              .filter((key, json) -> json != null && 
                     ("TripRequested".equals(json.path("eventType").asText()) || "TripCompleted".equals(json.path("eventType").asText())))
              .selectKey((key, json) -> json.path("pickupZone").asText())
              .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(JsonNode.class)))
              .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofHours(1), Duration.ofMinutes(1)))
              .aggregate(
                  () -> new ZoneStats(0, 0),
                  (key, value, aggregate) -> {
                      String type = value.path("eventType").asText();
                      if ("TripRequested".equals(type)) {
                          aggregate.setRequested(aggregate.getRequested() + 1);
                      } else if ("TripCompleted".equals(type)) {
                          aggregate.setCompleted(aggregate.getCompleted() + 1);
                      }
                      return aggregate;
                  },
                  Materialized.<String, ZoneStats, WindowStore<Bytes, byte[]>>as("analytics-stats")
                      .withValueSerde(new JsonSerde<>(ZoneStats.class))
              )
              .toStream()
              .foreach((windowedKey, stats) -> {
                   // persist aggregated stats to database at end of each window
                  String zoneId = windowedKey.key();
                  Instant windowStart = windowedKey.window().startTime();
                  LocalDateTime hourBucket = LocalDateTime.ofInstant(windowStart, ZoneId.systemDefault()).truncatedTo(ChronoUnit.HOURS);
                  
                  log.info("Saving analytics: Zone={}, Stats={}", zoneId, stats);
                  
                  ZoneAnalytics entity = new ZoneAnalytics(zoneId, hourBucket, stats.getRequested(), stats.getCompleted());
                  repository.save(entity);
              });

        return stream;
    }
    
    static class ZoneStats {
        private long requested;
        private long completed;
        public ZoneStats() {}
        public ZoneStats(long r, long c) { requested = r; completed = c; }
        public long getRequested() { return requested; }
        public void setRequested(long r) { requested = r; }
        public long getCompleted() { return completed; }
        public void setCompleted(long c) { completed = c; }
        // simple stats representation for debugging
        public String toString() { return "Req=" + requested + ", Comp=" + completed; }
    }

    static class JsonSerde<T> extends Serdes.WrapperSerde<T> {
        public JsonSerde(Class<T> targetType) {
            super(new org.springframework.kafka.support.serializer.JsonSerializer<>(), new org.springframework.kafka.support.serializer.JsonDeserializer<>(targetType));
        }
    }
}
