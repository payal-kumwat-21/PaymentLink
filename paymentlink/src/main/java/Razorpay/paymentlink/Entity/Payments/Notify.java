package Razorpay.paymentlink.Entity.Payments;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "notify_settings")
public class Notify {

    @Id
    @Column(name = "notify_id", length = 50)
    private String notifyId;

    @Column(name = "payment_link_id", nullable = false, length = 50)
    private String paymentLinkId; 

    @Column(name = "email")
    private Boolean email;

    @Column(name = "sms")
    private Boolean sms;
}