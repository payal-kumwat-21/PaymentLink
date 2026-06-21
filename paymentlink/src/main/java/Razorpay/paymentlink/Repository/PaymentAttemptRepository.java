package Razorpay.paymentlink.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Razorpay.paymentlink.Entity.Payments.PaymentAttempt;


@Repository
public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, String> {
 
}