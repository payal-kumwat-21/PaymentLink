package Razorpay.paymentlink.DTOs;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PaymentLinkResponse {
    private String id; // e.g., "plink_ExjpAUN3gVHrPJ"
    private Integer amount;
    private Integer amountPaid;
    private String currency;
    private Boolean acceptPartial;
    private Integer firstMinPartialAmount;
    private Boolean upiLink;
    private String description;
    private String referenceId;
    private String shortUrl;
    private String status; // "created", "partially_paid", "expired", "cancelled", "paid"
    private Boolean reminderEnable;
    private String callbackUrl;
    private String callbackMethod;
    private Long createdAt; // UNIX timestamp
    private Long updatedAt; // UNIX timestamp
    private Long expireBy; // UNIX timestamp
    private Long expiredAt; // UNIX timestamp
    private Long cancelledAt; // UNIX timestamp
    private String userId;
    private Boolean whatsappLink;

    private CustomerDTO customer;
    private NotifyDTO notify;
    private RemindersDTO reminders;
    private Map<String, String> notes;
    private List<PaymentAttemptDTO> payments;
     // Populated with captured history array, null otherwise
    private boolean hideTopbar;
    } 

