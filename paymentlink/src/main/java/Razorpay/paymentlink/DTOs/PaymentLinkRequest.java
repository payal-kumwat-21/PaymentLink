package Razorpay.paymentlink.DTOs;

import lombok.Data;
import java.util.Map;



@Data
public class PaymentLinkRequest {
    private Integer amount; // Mandatory: smallest unit of currency (e.g., 1000 = ₹10.00)
    private String currency; // Optional: e.g., "INR"
    private Boolean acceptPartial; // Optional
    private Integer firstMinPartialAmount; // Conditionally mandatory if acceptPartial is true
  
    private Boolean upiLink; // Mandatory for creating UPI Payment Link
    private String description; // Optional: max 2048 characters
    private String referenceId; // Optional: unique, max 40 characters
    private Long expireBy; // Optional: UNIX timestamp (must be at least 15 min in future)
    private String callbackUrl; // Optional
    private String callbackMethod; // Conditionally mandatory if callbackUrl is sent ("get")
    private Boolean reminderEnable; // Optional
    
    private CustomerDTO customer; // Optional nested object
    private NotifyDTO notify; // Optional nested object
    private Map<String, String> notes; // Optional: max 15 pairs, max 256 chars each value
    private Map<String, Object> options;
}