package Razorpay.paymentlink.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Razorpay.paymentlink.Entity.Payments.Notes;
import java.util.List;

@Repository
public interface NotesRepository extends JpaRepository<Notes, String> {
    // Allows your service layer to quickly retrieve all notes associated with a payment link
    List<Notes> findByPaymentLinkId(String paymentLinkId);
    //NotesRepository exposes a deletion hook declaration for update endpoint
    void deleteByPaymentLinkId(String paymentLinkId);
}