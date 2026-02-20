package com.payment_service.controller;

import com.payment_service.DTO.ProductRequest;
import com.payment_service.DTO.StripeResponse;
import com.payment_service.client.BookingClient;
import com.payment_service.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {

    private final StripeService stripeService;
    private final BookingClient bookingClient;

    @Value("${stripe.secretKey}")
    private String secretKey;

    public ProductCheckoutController(StripeService stripeService, BookingClient bookingClient) {
        this.stripeService = stripeService;
        this.bookingClient = bookingClient;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(@RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.CheckOutProducts(productRequest);
        return ResponseEntity.status(HttpStatus.OK).body(stripeResponse);
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam("session_id") String sessionId,
                                          @RequestParam("bookingId") Long bookingId) {
        try {
            Stripe.apiKey = secretKey; // ✅ IMPORTANT

            Session session = Session.retrieve(sessionId);
            String paymentStatus = session.getPaymentStatus();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                bookingClient.confirmBooking(bookingId, sessionId); // we will fix this next in step 2
                return ResponseEntity.ok("Payment successful. Booking confirmed.");
            }

            return ResponseEntity.status(400).body("Payment not completed. Status: " + paymentStatus);

        } catch (StripeException e) {
            return ResponseEntity.status(500).body("Stripe error: " + e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment cancelled";
    }
}
