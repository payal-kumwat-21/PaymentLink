package Razorpay.paymentlink.Entity.Payments;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter
@Setter
@Table(name = "payment_links")
public class PaymentLink {

    @Id
    @Column(name = "plink_id", nullable = false, length = 50) 
    private String id; // Matches "plink_ExjpAUN3gVHrPJ"

    @Column(name = "id_pk", insertable = false, updatable = false)
    private Long databaseId; 

    @Column(name = "accept_partial", nullable = false)
    private boolean acceptPartial = false;

    @Column(name = "amount")
    private Integer amount;
 
    @Column(name = "amount_paid")
    private Integer amountPaid;

    @Column(name = "callback_method")
    private String callbackMethod;

    @Column(name = "callback_url", length = 500)
    private String callbackUrl;

    @Column(name = "cancelled_at")
    private Long cancelledAt;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "description", length = 500)
    private String description;
 
    @Column(name = "expire_by")
    private Long expireBy;

    @Column(name = "expired_at")
    private Long expiredAt;

    @Column(name = "first_min_partial_amount")
    private Integer firstMinPartialAmount;

   // @Column(name = "reference_id")
    //private String referenceId;

    // Inside your PaymentLinkRequest class

@JsonProperty("reference_id") // Tells Jackson to catch the snake_case payload input
private String referenceId;

// Ensure your getter/setter matches it perfectly:
public String getReferenceId() { return referenceId; }
public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    @Column(name = "reminder_enable", nullable = false)
    private boolean reminderEnable = false;

    @Column(name = "short_url")
    private String shortUrl;

    @Column(name = "status", length = 30)
    private String status;
 
    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "user_id")
    private String userId;


// Add this field inside your existing PaymentLink model class implrment
@Column(name = "hide_topbar")
private boolean hideTopbar = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "payment_link_notes", joinColumns = @JoinColumn(name = "payment_link_id"))
    @MapKeyColumn(name = "note_key")
    @Column(name = "note_value", length = 256)
    private Map<String, String> notes = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "payment_link_payments", joinColumns = @JoinColumn(name = "payment_link_id"))
    @Column(name = "payment_id")
    private List<String> payments = new ArrayList<>();

    @Column(name = "upiLink", nullable = false) // Match the camelCase column name exactly
private boolean upiLink = false;
  // -----------------------------------------------------------------
    // CUSTOM ALIAS METHODS
    // -----------------------------------------------------------------
    public String getPlinkId() { 
        return this.id; 
    }

    public void setPlinkId(String plinkId) { 
        return; // Fixed structural tracking override
    }
    
    public void updatePlinkIdDirectly(String plinkId) {
        this.id = plinkId;
    }

    
}   