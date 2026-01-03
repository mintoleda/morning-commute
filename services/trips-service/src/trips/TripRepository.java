package trips;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// data access for trips
@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
    List<Trip> findByRiderID(String riderID);
}
