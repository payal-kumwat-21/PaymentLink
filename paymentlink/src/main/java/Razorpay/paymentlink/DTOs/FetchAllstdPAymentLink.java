package Razorpay.paymentlink.DTOs;



import java.util.ArrayList;

import java.util.List;

//import Razorpay.paymentlink.Entity.Payments.PaymentLink;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchAllstdPAymentLink {
    private Boolean acceptPartial;
    private Integer amount;
    private Integer amountPaid;
    private String callbackUrl;
    private String callbackMethod;
    private Long createdAt;      // Changed to Long to safely capture Unix timestamps
    private Long cancelledAt;    // Changed to Long to safely capture Unix timestamps
    private String currency;
    private Object customer;     // Using Object allows returning a clean array [] if empty
    private String description;
    private Long expireBy;       // Changed to Long to safely capture Unix timestamps
    private Long expiredAt;      // Changed to Long to safely capture Unix timestamps
    private Integer firstMinPartialAmount;
    private String id;
    private Boolean upiLink;
    private NotifyDTO notify;
    private Object notes;        // Using Object allows returning a clean array [] if empty
    private List<Object> payments = new ArrayList<>(); // Always defaults to empty array []
    private List<Object> reminders = new ArrayList<>(); // Always defaults to empty array []
    private String referenceId;
    private String shortUrl;     // Fixed: Changed from shorturl to camelCase shortUrl
    private String status;
    private Long updatedAt;      // Changed to Long to safely capture Unix timestamps
    private Boolean reminderEnable;
    private String userId;  
     }     // Fixed: Changed from userID to camelCase userId
