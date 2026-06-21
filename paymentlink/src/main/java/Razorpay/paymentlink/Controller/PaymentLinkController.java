package Razorpay.paymentlink.Controller;

import Razorpay.paymentlink.DTOs.PaymentLinkListResponse;
import Razorpay.paymentlink.DTOs.PaymentLinkRequest;
import Razorpay.paymentlink.DTOs.PaymentLinkResponse;
import Razorpay.paymentlink.DTOs.PaymentLinkUpdateRequest;
import Razorpay.paymentlink.Service.PaymentLinkService;
import lombok.RequiredArgsConstructor;

//import java.util.HashMap;

import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment-links")
@RequiredArgsConstructor // Automatically injects PaymentLinkService via constructor injection
public class PaymentLinkController {

    //private static final PaymentLinkk PaymentLinkk = null;
    private final PaymentLinkService paymentLinkService;

   @PostMapping("/")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestBody PaymentLinkRequest request) {
        
        // Dynamic Routing: Check if the payload explicitly requests a UPI Link
        if (Boolean.TRUE.equals(request.getUpiLink())) {
            PaymentLinkResponse upiResponse = paymentLinkService.createUpiPaymentLink(request);
            return new ResponseEntity<>(upiResponse, HttpStatus.CREATED);
        }
        
        // Fallback: Default to standard payment link processing flow
        PaymentLinkResponse standardResponse = paymentLinkService.createStandardPaymentLink(request);
        return new ResponseEntity<>(standardResponse, HttpStatus.CREATED);
    }

  
    // Add this method to PaymentLinkController.java
@GetMapping("/")
public ResponseEntity<PaymentLinkListResponse> getAllPaymentLinks(
        @RequestParam(value = "reference_id", required = false) String referenceId,
        @RequestParam(value = "payment_id", required = false) String paymentId) {
    
    // Razorpay constraint check: reference_id max character length is 40
    if (referenceId != null && referenceId.length() > 40) {
        throw new IllegalArgumentException("The receipt may not be greater than 40 characters.");
    }
    
    PaymentLinkListResponse response = paymentLinkService.getAllPaymentLinks(referenceId, paymentId);
    return ResponseEntity.ok(response);
}


     @GetMapping("/{id}")
    public ResponseEntity<PaymentLinkResponse> getPaymentLinkById(@PathVariable("id") String id) {
        
        // 1. Razorpay global sanity check validation rule
        if (id == null || !id.startsWith("plink_")) {
            throw new IllegalArgumentException("invalid input [strippedId] = [" + (id != null ? id : "null") + "]");
        }
        
        // 2. Retrieve the processed response from the service layer
        PaymentLinkResponse response = paymentLinkService.getPaymentLinkById(id);
        
        return ResponseEntity.ok(response);
    }



     @PatchMapping("/{id}")
public ResponseEntity<?> updatePaymentLink1(
        @PathVariable("id") String id, 
        @RequestBody PaymentLinkUpdateRequest request) {
    try {
        // 1. Global Sanity Checks
        if (id == null || !id.startsWith("plink_")) {
            throw new IllegalArgumentException("invalid input [strippedId] = [" + (id != null ? id : "null") + "]");
        }
        if (request.getReferenceId() != null && request.getReferenceId().length() > 40) {
            throw new IllegalArgumentException("The receipt may not be greater than 40 characters.");
        }

        // 2. Fetch the existing link to check its type dynamically
        // (Assuming getPaymentLinkById returns an object with an isUpiLink() method)
        PaymentLinkResponse existingLink = paymentLinkService.getPaymentLinkById(id);
        
        PaymentLinkResponse updatedLink;
        if (existingLink.getUpiLink()) {
            // Internally routes to your UPI business logic rules
            updatedLink = paymentLinkService.updateUpiPaymentLink(id, request);
        } else {
            // Internally routes to your Standard business logic rules
            updatedLink = paymentLinkService.updatePaymentLink(id, request);
        }
        
        return ResponseEntity.ok(updatedLink);

    } catch (IllegalArgumentException e) {
        // Structured Razorpay Business Logic failure message (400 Bad Request)
        java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("code", "BAD_REQUEST_ERROR");
        details.put("description", e.getMessage());
        errorMap.put("error", details);
        return ResponseEntity.status(400).body(errorMap);
        
    } catch (RuntimeException e) {
        // Resource missing or system error handler (404 Not Found)
        java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("code", "BAD_REQUEST_ERROR");
        details.put("description", e.getMessage());
        errorMap.put("error", details);
        return ResponseEntity.status(404).body(errorMap);
    }
}

    


    @PatchMapping("/stdUpdate/{id}")
    public ResponseEntity<?> updatePaymentLink(@PathVariable String id, @RequestBody PaymentLinkUpdateRequest request) {
        try {
            PaymentLinkResponse updatedLink = paymentLinkService.updatePaymentLink(id, request);
            return ResponseEntity.ok(updatedLink);
        } catch (IllegalArgumentException e) {
            // Replicating a structured Razorpay Business Logic failure message
            java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("code", "BAD_REQUEST_ERROR");
            details.put("description", e.getMessage());
            errorMap.put("error", details);
            return ResponseEntity.status(400).body(errorMap);
        } catch (RuntimeException e) {
            java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
            java.util.Map<String, String> details = new java.util.HashMap<>();
            details.put("code", "BAD_REQUEST_ERROR");
            details.put("description", e.getMessage());
            errorMap.put("error", details);
            return ResponseEntity.status(404).body(errorMap);
        }
    }

/**
 * POST Cancel Payment Link upi Endpoint
 * POST /api/v1/payment-links/{id}/cancel
 */
@PostMapping("/{id}/cancel")
public ResponseEntity<?> cancelPaymentLink(@PathVariable("id") String id) {
    try {
        // 1. Prefix Sanity Check Validation
        if (id == null || !id.startsWith("plink_")) {
            throw new IllegalArgumentException("invalid input [strippedId] = [" + (id != null ? id : "null") + "]");
        }

        // 2. Execute cancellation via service
        PaymentLinkResponse response = paymentLinkService.cancelPaymentLink(id);
        return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
        // Structured Razorpay Business Logic failure structure (400 Bad Request)
        java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("code", "BAD_REQUEST_ERROR");
        details.put("description", e.getMessage());
        errorMap.put("error", details);
        return ResponseEntity.status(400).body(errorMap);
        
    } catch (RuntimeException e) {
        // Fallback catch-all for missing items
        java.util.Map<String, Object> errorMap = new java.util.HashMap<>();
        java.util.Map<String, String> details = new java.util.HashMap<>();
        details.put("code", "BAD_REQUEST_ERROR");
        details.put("description", e.getMessage());
        errorMap.put("error", details);
        return ResponseEntity.status(404).body(errorMap);
    }
}
}