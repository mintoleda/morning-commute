# morning-commute

an event-driven ride-hailing simulation built with java, spring boot, and kafka.

## flow
1. **request**: a rider creates a trip via the trips-service.
2. **events**: a `TripRequested` event is pushed to kafka.
3. **pricing**: the pricing-service calculates zone demand in real-time.
4. **analytics**: the analytics-service aggregates demand metrics into 1-hour windows and saves them to postgres.

## tech
- java 21 & spring boot 3.5
- kafka & kafka streams
- postgres for olatp and analytics
- docker compose for local infra

## services
- `trips-service`: rest api for trip lifecycle (create, complete, retrieve).
- `pricing-service`: calculates demand multipliers based on active trips.
- `analytics-service`: computes hourly zone metrics using kafka streams.

## setup & run
build the jars:
```bash
./mvnw clean package -DskipTests -f common/pom.xml
./mvnw clean package -DskipTests -f services/trips-service/pom.xml
./mvnw clean package -DskipTests -f services/pricing-service/pom.xml
./mvnw clean package -DskipTests -f services/analytics-service/pom.xml
```

spin up the infrastructure:
```bash
cd infra && docker compose up -d --build
```

verify status:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

## manual testing

### trip lifecycle
create a trip:
```bash
curl -X POST http://localhost:8080/trips \
  -H "Content-Type: application/json" \
  -d '{"riderID": "rider-123", "pickupZone": "Z12", "dropoffZone": "Z05"}'
```

check status:
```bash
curl http://localhost:8080/trips/{trip-id}
```

complete trip:
```bash
curl -X POST http://localhost:8080/trips/{trip-id}/complete
```

### query services
get pricing for a zone:
```bash
curl http://localhost:8081/pricing/zone/Z12
```

view aggregated analytics:
```bash
docker exec -it postgres psql -U user -d rides_db \
  -c "SELECT * FROM analytics_zone_hourly ORDER BY hour_bucket DESC LIMIT 10;"
```

### troubleshooting
- ensure ports 8080-8082, 5432, 9092 are free.
- logs: `docker compose logs -f <service-name>`
- restart: `docker compose restart <service-name>`
