package trips;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // create a new trip
    @PostMapping
    public Trip createTrip(@RequestBody Trip trip) {
        tripService.createTrip(trip);
        return trip;
    }

    // complete an existing trip
    @PostMapping("/{id}/complete")
    public void completeTrip(@PathVariable String id) {
        tripService.completeTrip(id);
    }

    // retrieve trips by rider
    @GetMapping(params = "riderId")
    public List<Trip> getTripsByRider(@RequestParam String riderId) {
        return tripService.getTripsByRider(riderId);
    }

    // retrive all trips
    @GetMapping("")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    // retrieve trip details
    @GetMapping("/{id}")
    public Trip getTrip(@PathVariable String id) {
        return tripService.getTrip(id);
    }

    // cancel trip
    @PostMapping("/{id}/cancel")
    public void cancelTrip(@PathVariable String id) {
        tripService.cancelTrip(id);
    }
}
