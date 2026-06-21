package Razorpay.paymentlink.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class PaymentLinkUpdateRequest {

    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("expire_by")
    private Long expireBy;

    @JsonProperty("reminder_enable")
    private Boolean reminderEnable;

    private Map<String, String> notes;

    // Getters and Setters
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public Long getExpireBy() { return expireBy; }
    public void setExpireBy(Long expireBy) { this.expireBy = expireBy; }

    public Boolean getReminderEnable() { return reminderEnable; }
    public void setReminderEnable(Boolean reminderEnable) { this.reminderEnable = reminderEnable; }

    public Map<String, String> getNotes() { return notes; }
    public void setNotes(Map<String, String> notes) { this.notes = notes; }
}