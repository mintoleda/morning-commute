package pricing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    // return pricing details
    @GetMapping("/zone/{zoneId}")
    public Map<String, Object> getPricing(@PathVariable String zoneId) {
        Map<String, Object> response = new HashMap<>();
        response.put("zone_id", zoneId);
        response.put("active_trips", pricingService.getActiveTrips(zoneId));
        response.put("multiplier", pricingService.getMultiplier(zoneId));
        return response;
    }
}
