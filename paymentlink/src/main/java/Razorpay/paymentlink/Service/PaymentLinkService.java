package Razorpay.paymentlink.Service;

import Razorpay.paymentlink.DTOs.PaymentLinkListResponse;
import Razorpay.paymentlink.DTOs.PaymentLinkRequest;
import Razorpay.paymentlink.DTOs.PaymentLinkResponse;
import Razorpay.paymentlink.DTOs.PaymentLinkUpdateRequest;


public interface PaymentLinkService {
       PaymentLinkResponse createStandardPaymentLink(PaymentLinkRequest request);
    //PaymentLinkListResponse getAllPaymentLinks();
    PaymentLinkListResponse getAllPaymentLinks(String referenceId, String paymentId);
    PaymentLinkResponse getPaymentLinkById(String id);
    PaymentLinkResponse updateUpiPaymentLink(String id, PaymentLinkUpdateRequest request);
    PaymentLinkResponse updatePaymentLink(String id, PaymentLinkUpdateRequest request);
    PaymentLinkResponse cancelPaymentLink(String id);
    PaymentLinkResponse createUpiPaymentLink(PaymentLinkRequest request);

}

