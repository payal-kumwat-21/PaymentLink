package Razorpay.paymentlink.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Razorpay.paymentlink.Entity.Payments.PaymentLink;

@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, String> {

    List<PaymentLink> findByReferenceId(String referenceId);
    List<PaymentLink> findAll();
   
    PaymentLink findByPlinkId(String plinkId);
}