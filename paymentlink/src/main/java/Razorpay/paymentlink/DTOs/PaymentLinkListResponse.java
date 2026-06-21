package Razorpay.paymentlink.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkListResponse {
    private List<PaymentLinkResponse> paymentLinks;
}

//second endpoint 