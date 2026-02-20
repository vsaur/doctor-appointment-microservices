package com.payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "booking-service", url = "${BOOKING_SERVICE_URL}")
public interface BookingClient {

    @PostMapping("/api/v1/booking/confirm")
    String confirmBooking(@RequestParam("bookingId") Long bookingId,
                          @RequestParam("sessionId") String sessionId);
}