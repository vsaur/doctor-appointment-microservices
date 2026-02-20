package com.booking_service.client;


import com.booking_service.DTO.ProductRequest;
import com.booking_service.DTO.StripeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${PAYMENT_SERVICE_URL}")
public interface PaymentClient {

    @PostMapping("/product/v1/checkout")
    StripeResponse checkout(@RequestBody ProductRequest productRequest);
}
