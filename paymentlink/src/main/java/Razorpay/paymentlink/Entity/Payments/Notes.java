package Razorpay.paymentlink.Entity.Payments;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

@Entity
@Data
@Table(name = "notes")
public class Notes implements Persistable<String> {

    @Id
   @Column(name = "note_id")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String noteId;

    @Column(name = "payment_link_id")
    private String paymentLinkId;

    @Column(name = "note_key")
    private String noteKey;

    @Column(name = "note_value")
    private String noteValue;

    // ---- UPDATE THIS BLOCK FOR DYNAMIC LIFECYCLE TRACKING ----
    @Transient
    private boolean isUpdateAction = false; // Custom flag to toggle state

    @Override
    public String getId() {
        return this.noteId;
    }

    @Override
    @Transient
    public boolean isNew() {
        // If it's marked as an update action, return false (forces merge/update)
        // Otherwise, return true (forces raw insert)
        return !isUpdateAction; 
    }
}