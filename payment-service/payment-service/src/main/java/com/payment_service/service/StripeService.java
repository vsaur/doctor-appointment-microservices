package com.payment_service.service;

import com.payment_service.DTO.ProductRequest;
import com.payment_service.DTO.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    // ✅ Stripe secret key from env: STRIPE_SECRET_KEY
    @Value("${stripe.secretKey}")
    private String secretKey;

    // ✅ Public base URL of payment-service from env: PAYMENT_PUBLIC_URL
    // Example: http://localhost:8085  OR  https://your-payment.onrender.com
    @Value("${payment.public.url}")
    private String paymentPublicUrl;

    public StripeResponse CheckOutProducts(ProductRequest productRequest) {

        // ✅ Set Stripe key before calling Stripe API
        Stripe.apiKey = secretKey;

        // 1) Product data (what shows on Stripe checkout)
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        // 2) Price data (currency + amount in smallest unit)
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "INR")
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)
                        .build();

        // 3) Line item (quantity + priceData)
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)
                        .build();

        // ✅ IMPORTANT: Success/Cancel URLs must NOT be localhost for production.
        // We use paymentPublicUrl so it works locally + deployed.
        String successUrl = paymentPublicUrl
                + "/product/v1/success?session_id={CHECKOUT_SESSION_ID}&bookingId="
                + productRequest.getBookingId();

        String cancelUrl = paymentPublicUrl + "/product/v1/cancel";

        // 4) Create checkout session params
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(lineItem)
                .build();

        try {
            // 5) Create Stripe session
            Session session = Session.create(params);

            // 6) Return to booking-service (Feign) to redirect frontend
            StripeResponse sr = new StripeResponse();
            sr.setStatus("success");
            sr.setMessage("payment session created");
            sr.setSessionId(session.getId());
            sr.setSessionUrl(session.getUrl());
            return sr;

        } catch (StripeException e) {
            StripeResponse sr = new StripeResponse();
            sr.setStatus("failed");
            sr.setMessage("stripe error: " + e.getMessage());
            return sr;
        }
    }
}