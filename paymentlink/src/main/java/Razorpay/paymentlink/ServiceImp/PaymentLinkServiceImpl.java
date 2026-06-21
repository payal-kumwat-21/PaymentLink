package Razorpay.paymentlink.ServiceImp;

import Razorpay.paymentlink.DTOs.*;
import Razorpay.paymentlink.Entity.Payments.Customer;
import Razorpay.paymentlink.Entity.Payments.Notes;
import Razorpay.paymentlink.Entity.Payments.Notify;
import Razorpay.paymentlink.Entity.Payments.PaymentLink;
import Razorpay.paymentlink.Entity.Payments.Reminders;
import Razorpay.paymentlink.Repository.CustomerRepository;
import Razorpay.paymentlink.Repository.NotesRepository;
import Razorpay.paymentlink.Repository.NotifyRepository;
import Razorpay.paymentlink.Repository.PaymentLinkRepository;
import Razorpay.paymentlink.Repository.RemindersRepository;
import Razorpay.paymentlink.Service.PaymentLinkService;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentLinkServiceImpl implements PaymentLinkService {

    // Injecting all 5 independent repositories
    private final PaymentLinkRepository paymentLinkRepository;
    private final CustomerRepository customerRepository;
    private final NotifyRepository notifyRepository;
    private final RemindersRepository remindersRepository;
    private final NotesRepository notesRepository;

   
        
    @Override
    @Transactional // Ensures database consistency across all 5 independent saves
    public PaymentLinkResponse createStandardPaymentLink(PaymentLinkRequest request) {
      
        // 1. Generate a mock Razorpay style unique Link ID
        String standardLinkId = "plink_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        long currentTimeMillis = System.currentTimeMillis() / 1000; // UNIX Timestamp format

        // 2. Map and Save the core PaymentLink entity
        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId(standardLinkId);
        paymentLink.setAmount(request.getAmount());
        paymentLink.setAmountPaid(0); // Brand new link, no amount paid yet
        paymentLink.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        
        // Null-safe unboxing for booleans
        paymentLink.setAcceptPartial(Boolean.TRUE.equals(request.getAcceptPartial()));
        paymentLink.setReminderEnable(Boolean.TRUE.equals(request.getReminderEnable()));
        
        // Handle upiLink safely if your entity has the setter
        paymentLink.setUpiLink(Boolean.TRUE.equals(request.getUpiLink())); 
        
        paymentLink.setFirstMinPartialAmount(request.getFirstMinPartialAmount());
        paymentLink.setDescription(request.getDescription());
        paymentLink.setReferenceId(request.getReferenceId());
        paymentLink.setCallbackUrl(request.getCallbackUrl());
        paymentLink.setCallbackMethod(request.getCallbackMethod());
        paymentLink.setStatus("created"); // Default status as per documentation
        paymentLink.setCreatedAt(currentTimeMillis);
        paymentLink.setUpdatedAt(currentTimeMillis);
        paymentLink.setExpireBy(request.getExpireBy());
        paymentLink.setShortUrl("https://rzp.io/i/" + UUID.randomUUID().toString().substring(0, 7));
        paymentLink.setUserId("usr_mockAdmin123");
        
        // Handling ElementCollection map mapping inside PaymentLink entity
        if (request.getNotes() != null) {
            paymentLink.setNotes(request.getNotes());
        }

        paymentLinkRepository.save(paymentLink);

        // 3. Extract and save Customer into its independent table
        CustomerDTO customerDTO = null;
        if (request.getCustomer() != null) {
            Customer customer = new Customer();
            customer.setCustomerId("cust_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            customer.setPaymentLinkId(standardLinkId); // Linked manually via string!
            customer.setName(request.getCustomer().getName());
            customer.setEmail(request.getCustomer().getEmail());
            customer.setContact(request.getCustomer().getContact());
            customerRepository.save(customer);

            // Populate response sub-object
            customerDTO = new CustomerDTO();
            customerDTO.setName(customer.getName());
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setContact(customer.getContact());
        }

        // 4. Extract and save Notification Settings into its independent table
        NotifyDTO notifyDTO = null;
        if (request.getNotify() != null) {
            Notify notify = new Notify();
            notify.setNotifyId("notif_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            notify.setPaymentLinkId(standardLinkId); // Linked manually via string!
            notify.setEmail(request.getNotify().getEmail());
            notify.setSms(request.getNotify().getSms());
            notifyRepository.save(notify);

            // Populate response sub-object
            notifyDTO = new NotifyDTO();
            notifyDTO.setEmail(notify.getEmail());
            notifyDTO.setSms(notify.getSms());
        }

        // 5. Initialize and save Reminders state into its independent table
        Reminders reminders = new Reminders();
        reminders.setReminderId("rem_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        reminders.setPaymentLinkId(standardLinkId); // Linked manually via string!
        reminders.setStatus("pending"); // Default state for a newly generated link
        remindersRepository.save(reminders);

        RemindersDTO remindersDTO = new RemindersDTO();
        remindersDTO.setStatus(reminders.getStatus());

        // 6. Loop and save audit rows into the independent Notes entity table
        if (request.getNotes() != null) {
            request.getNotes().forEach((key, value) -> {
                Notes noteEntity = new Notes();
                noteEntity.setNoteId("note_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
                noteEntity.setPaymentLinkId(standardLinkId); // Linked manually via string!
                noteEntity.setNoteKey(key);
                noteEntity.setNoteValue(value);
                notesRepository.save(noteEntity);
            });
        }

        // 7. Assemble the final Razorpay compliance nested Response DTO
        PaymentLinkResponse response = new PaymentLinkResponse();
        response.setId(paymentLink.getId());
        response.setAmount(paymentLink.getAmount());
        response.setAmountPaid(paymentLink.getAmountPaid());
        response.setCurrency(paymentLink.getCurrency());
        response.setAcceptPartial(paymentLink.isAcceptPartial());
        response.setFirstMinPartialAmount(paymentLink.getFirstMinPartialAmount());
        response.setDescription(paymentLink.getDescription());
        response.setReferenceId(paymentLink.getReferenceId());
        response.setShortUrl(paymentLink.getShortUrl());
        response.setStatus(paymentLink.getStatus());
        response.setReminderEnable(paymentLink.isReminderEnable());
        response.setCallbackUrl(paymentLink.getCallbackUrl());
        response.setCallbackMethod(paymentLink.getCallbackMethod());
        response.setCreatedAt(paymentLink.getCreatedAt());
        response.setUpdatedAt(paymentLink.getUpdatedAt());
        response.setExpireBy(paymentLink.getExpireBy());
        response.setExpiredAt(0L);
        response.setCancelledAt(0L);
        response.setUserId(paymentLink.getUserId());
        response.setWhatsappLink(false);

        // Bind the child responses
        response.setCustomer(customerDTO);
        response.setNotify(notifyDTO);
        response.setReminders(remindersDTO);
        response.setNotes(request.getNotes());
        response.setPayments(new ArrayList<>()); // Empty list array as per creation spec docs

        // Add this line in BOTH service methods right before "return response;"
        response.setUpiLink(paymentLink.isUpiLink());

        return response;
    }




    @Override
@Transactional
public PaymentLinkResponse createUpiPaymentLink(PaymentLinkRequest request) {
    
    // 1. Enforce Razorpay UPI Business Rule Guards
    if (Boolean.TRUE.equals(request.getAcceptPartial())) {
        throw new IllegalArgumentException("partial payment not supported in upi link.");
    }
    if (request.getCurrency() != null && !"INR".equalsIgnoreCase(request.getCurrency())) {
        throw new IllegalArgumentException("upi is currently supported only in indian currency");
    }
    if (request.getAmount() != null && request.getAmount() < 100) {
        throw new IllegalArgumentException("amount: amount should be minimum 100 for INR.");
    }

    // 2. Generate a unique Link ID
    String upiLinkId = "plink_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
    long currentTimeMillis = System.currentTimeMillis() / 1000;

    // 3. Map and Save core Entity with upiLink locked to true
    PaymentLink paymentLink = new PaymentLink();
    paymentLink.setId(upiLinkId);
    paymentLink.setAmount(request.getAmount());
    paymentLink.setAmountPaid(0);
    paymentLink.setCurrency("INR"); // UPI is always INR
    paymentLink.setAcceptPartial(false); // UPI never accepts partials
    paymentLink.setReminderEnable(Boolean.TRUE.equals(request.getReminderEnable()));
    
    // SAFE IMPLEMENTATION: Force to true for UPI flows
    paymentLink.setUpiLink(true); 

    paymentLink.setDescription(request.getDescription());
    paymentLink.setReferenceId(request.getReferenceId());
    paymentLink.setCallbackUrl(request.getCallbackUrl());
    paymentLink.setCallbackMethod(request.getCallbackMethod());
    paymentLink.setStatus("created");
    paymentLink.setCreatedAt(currentTimeMillis);
    paymentLink.setUpdatedAt(currentTimeMillis);
    paymentLink.setExpireBy(request.getExpireBy());
    paymentLink.setShortUrl("https://rzp.io/i/" + UUID.randomUUID().toString().substring(0, 7));
    paymentLink.setUserId("usr_mockAdmin123");

    if (request.getNotes() != null) {
        paymentLink.setNotes(request.getNotes());
    }

    paymentLinkRepository.save(paymentLink);

    // 4. Save Customer records if present
    CustomerDTO customerDTO = null;
    if (request.getCustomer() != null) {
        Customer customer = new Customer();
        customer.setCustomerId("cust_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        customer.setPaymentLinkId(upiLinkId);
        customer.setName(request.getCustomer().getName());
        customer.setEmail(request.getCustomer().getEmail());
        customer.setContact(request.getCustomer().getContact());
        customerRepository.save(customer);

        customerDTO = new CustomerDTO();
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setContact(customer.getContact());
    }

    // 5. Save Notifications state if present
    NotifyDTO notifyDTO = null;
    if (request.getNotify() != null) {
        Notify notify = new Notify();
        notify.setNotifyId("notif_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        notify.setPaymentLinkId(upiLinkId);
        notify.setEmail(request.getNotify().getEmail());
        notify.setSms(request.getNotify().getSms());
        notifyRepository.save(notify);

        notifyDTO = new NotifyDTO();
        notifyDTO.setEmail(notify.getEmail());
        notifyDTO.setSms(notify.getSms());
    }

    // 6. Save Reminders state
    Reminders reminders = new Reminders();
    reminders.setReminderId("rem_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
    reminders.setPaymentLinkId(upiLinkId);
    reminders.setStatus("pending");
    remindersRepository.save(reminders);

    RemindersDTO remindersDTO = new RemindersDTO();
    remindersDTO.setStatus(reminders.getStatus());

    // 7. Save Notes audit log
    if (request.getNotes() != null) {
        request.getNotes().forEach((key, value) -> {
            Notes noteEntity = new Notes();
            noteEntity.setNoteId("note_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
            noteEntity.setPaymentLinkId(upiLinkId);
            noteEntity.setNoteKey(key);
            noteEntity.setNoteValue(value);
            notesRepository.save(noteEntity);
        });
    }

    // 8. Build the complete nested response object
    PaymentLinkResponse response = new PaymentLinkResponse();
    response.setId(paymentLink.getId());
    response.setAmount(paymentLink.getAmount());
    response.setAmountPaid(paymentLink.getAmountPaid());
    response.setCurrency(paymentLink.getCurrency());
    response.setAcceptPartial(paymentLink.isAcceptPartial());
    response.setFirstMinPartialAmount(null);
    response.setDescription(paymentLink.getDescription());
    response.setReferenceId(paymentLink.getReferenceId());
    response.setShortUrl(paymentLink.getShortUrl());
    response.setStatus(paymentLink.getStatus());
    response.setReminderEnable(paymentLink.isReminderEnable());
    response.setCallbackUrl(paymentLink.getCallbackUrl());
    response.setCallbackMethod(paymentLink.getCallbackMethod());
    response.setCreatedAt(paymentLink.getCreatedAt());
    response.setUpdatedAt(paymentLink.getUpdatedAt());
    response.setExpireBy(paymentLink.getExpireBy());
    response.setExpiredAt(0L);
    response.setCancelledAt(0L);
    response.setUserId(paymentLink.getUserId());
    response.setWhatsappLink(false);

    response.setCustomer(customerDTO);
    response.setNotify(notifyDTO);
    response.setReminders(remindersDTO);
    response.setNotes(request.getNotes());
    response.setPayments(new ArrayList<>());


    // Add this line in BOTH service methods right before "return response;"
response.setUpiLink(paymentLink.isUpiLink());

    return response;
}



//second endpoint

     @Override
@Transactional // Keeps the read operations stable across the collections
public PaymentLinkListResponse getAllPaymentLinks(String referenceId, String paymentId) {
    // 1. Fetch all root payment link entities
    java.util.List<PaymentLink> links = paymentLinkRepository.findAll();
    java.util.List<PaymentLinkResponse> compiledResponses = new ArrayList<>();

    for (PaymentLink paymentLink : links) {
        
        // CRITICAL FILTER 1: Must be a UPI link
        if (!paymentLink.isUpiLink()) {
            continue;
        }

        // CRITICAL FILTER 2: Match reference_id if provided
        if (referenceId != null && !referenceId.equals(paymentLink.getReferenceId())) {
            continue;
        }

        String linkId = paymentLink.getId();

        // 2. Fetch and reconstruct Customer sub-object
        CustomerDTO customerDTO = customerRepository.findByPaymentLinkId(linkId)
            .map(customer -> {
                CustomerDTO dto = new CustomerDTO();
                dto.setName(customer.getName());
                dto.setEmail(customer.getEmail());
                dto.setContact(customer.getContact());
                return dto;
            }).orElse(null);

        // 3. Fetch and reconstruct Notify sub-object
        NotifyDTO notifyDTO = notifyRepository.findByPaymentLinkId(linkId)
            .map(notify -> {
                NotifyDTO dto = new NotifyDTO();
                dto.setEmail(notify.getEmail());
                dto.setSms(notify.getSms());
                return dto;
            }).orElse(null);

        // 4. Fetch and reconstruct Reminders sub-object
        RemindersDTO remindersDTO = remindersRepository.findByPaymentLinkId(linkId)
            .map(reminder -> {
                RemindersDTO dto = new RemindersDTO();
                dto.setStatus(reminder.getStatus());
                return dto;
            }).orElse(null);

        // 5. Fetch and reconstruct Notes Map
        java.util.Map<String, String> notesMap = new java.util.HashMap<>();
        notesRepository.findByPaymentLinkId(linkId).forEach(note -> {
            notesMap.put(note.getNoteKey(), note.getNoteValue());
        });

        // 6. Build the item response structure
        PaymentLinkResponse item = new PaymentLinkResponse();
        item.setId(paymentLink.getId());
        item.setAmount(paymentLink.getAmount());
        item.setAmountPaid(paymentLink.getAmountPaid());
        item.setCurrency(paymentLink.getCurrency());
        item.setAcceptPartial(paymentLink.isAcceptPartial());
        item.setFirstMinPartialAmount(paymentLink.getFirstMinPartialAmount());
        item.setDescription(paymentLink.getDescription());
        item.setReferenceId(paymentLink.getReferenceId());
        item.setShortUrl(paymentLink.getShortUrl());
        item.setStatus(paymentLink.getStatus());
        item.setReminderEnable(paymentLink.isReminderEnable());
        item.setCallbackUrl(paymentLink.getCallbackUrl());
        item.setCallbackMethod(paymentLink.getCallbackMethod());
        item.setCreatedAt(paymentLink.getCreatedAt());
        item.setUpdatedAt(paymentLink.getUpdatedAt());
        item.setExpireBy(paymentLink.getExpireBy());
        item.setExpiredAt(0L);
        item.setCancelledAt(0L);
        item.setUserId(paymentLink.getUserId());
        item.setUpiLink(paymentLink.isUpiLink());
        item.setWhatsappLink(false);

        // Attach mapped records
        item.setCustomer(customerDTO);
        item.setNotify(notifyDTO);
        item.setReminders(remindersDTO);
        item.setNotes(notesMap.isEmpty() ? paymentLink.getNotes() : notesMap);
        item.setPayments(new ArrayList<>()); // Kept empty as per core documentation spec

        // CRITICAL FILTER 3: Filter by payment_id if requested (Applied on the final populated object)
        if (paymentId != null && (item.getPayments() == null || 
            item.getPayments().stream().noneMatch(p -> paymentId.equals(p.getPaymentId())))) {
            continue;
        }

        compiledResponses.add(item);
    }

    return new PaymentLinkListResponse(compiledResponses);
}



// Add this method to PaymentLinkServiceImpl.java
@Override
@Transactional(readOnly = true) // Optimal performance configuration for lookup reads
public PaymentLinkResponse getPaymentLinkById(String id) {
    
    // 1. Core verification check: Find the entity row or fail gracefully
    PaymentLink paymentLink = paymentLinkRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("The id provided does not exist"));

    // 2. Fetch and reconstruct Customer sub-object
    CustomerDTO customerDTO = customerRepository.findByPaymentLinkId(id)
        .map(customer -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setName(customer.getName());
            dto.setEmail(customer.getEmail());
            dto.setContact(customer.getContact());
            return dto;
        }).orElse(null);

    // 3. Fetch and reconstruct Notify sub-object
    NotifyDTO notifyDTO = notifyRepository.findByPaymentLinkId(id)
        .map(notify -> {
            NotifyDTO dto = new NotifyDTO();
            dto.setEmail(notify.getEmail());
            dto.setSms(notify.getSms());
            return dto;
        }).orElse(null);

    // 4. Fetch and reconstruct Reminders sub-object
    RemindersDTO remindersDTO = remindersRepository.findByPaymentLinkId(id)
        .map(reminder -> {
            RemindersDTO dto = new RemindersDTO();
            dto.setStatus(reminder.getStatus());
            return dto;
        }).orElse(null);

    // 5. Fetch and reconstruct Notes Map
    java.util.Map<String, String> notesMap = new java.util.HashMap<>();
    notesRepository.findByPaymentLinkId(id).forEach(note -> {
        notesMap.put(note.getNoteKey(), note.getNoteValue());
    });

    // 6. Build response object and apply rules dynamically based on link type
    PaymentLinkResponse response = new PaymentLinkResponse();
    response.setId(paymentLink.getId());
    response.setAmount(paymentLink.getAmount());
    response.setAmountPaid(paymentLink.getAmountPaid());
    response.setCurrency(paymentLink.getCurrency());
    response.setDescription(paymentLink.getDescription());
    response.setReferenceId(paymentLink.getReferenceId());
    response.setShortUrl(paymentLink.getShortUrl());
    response.setStatus(paymentLink.getStatus());
    response.setReminderEnable(paymentLink.isReminderEnable());
    response.setCallbackUrl(paymentLink.getCallbackUrl());
    response.setCallbackMethod(paymentLink.getCallbackMethod());
    response.setCreatedAt(paymentLink.getCreatedAt());
    response.setUpdatedAt(paymentLink.getUpdatedAt());
    response.setExpireBy(paymentLink.getExpireBy());
    response.setUserId(paymentLink.getUserId());
    response.setUpiLink(paymentLink.isUpiLink()); // Standard = false, UPI = true
    response.setWhatsappLink(false);
    
    // Explicit hardcoded overrides matching Razorpay behavior
    response.setExpiredAt(0L);
    response.setCancelledAt(0L);

    // 7. Core Route Customizations
    if (paymentLink.isUpiLink()) {
        // --- UPI-Specific Specification Processing Rules ---
        response.setAcceptPartial(false); // Forced completely to false for direct intent payments
        response.setFirstMinPartialAmount((int) 0L);
    } else {
        // --- Standard-Specific Specification Processing Rules ---
        response.setAcceptPartial(paymentLink.isAcceptPartial());
        response.setFirstMinPartialAmount(paymentLink.getFirstMinPartialAmount());
    }

    // 8. Bind sub-objects to final schema
    response.setCustomer(customerDTO);
    response.setNotify(notifyDTO);
    response.setReminders(remindersDTO);
    response.setNotes(notesMap.isEmpty() ? paymentLink.getNotes() : notesMap);
    response.setPayments(new ArrayList<>()); // Maintained clean and empty until capture flows

    return response;
}


    //Update
    @Override
    @Transactional
    public PaymentLinkResponse updatePaymentLink(String id, PaymentLinkUpdateRequest request) {
        // 1. Fetch core entity or fail
        PaymentLink paymentLink = paymentLinkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("The id provided does not exist"));

        // 2. Razorpay Guard Rule: Check current status state
        String currentStatus = paymentLink.getStatus().toLowerCase();
        if (!currentStatus.equals("created") && !currentStatus.equals("partially_paid")) {
            throw new IllegalArgumentException("update can only be made in created or partially paid state");
        }

        // 3. Selective field updates (Null-safe checks)
        if (request.getReferenceId() != null) {
            paymentLink.setReferenceId(request.getReferenceId());
        }
        if (request.getExpireBy() != null) {
            paymentLink.setExpireBy(request.getExpireBy());
        }
        if (request.getReminderEnable() != null) {
            paymentLink.setReminderEnable(request.getReminderEnable());
        }

        // Capture update timestamp (Unix Epoch seconds)
        paymentLink.setUpdatedAt(System.currentTimeMillis() / 1000L);
        paymentLinkRepository.save(paymentLink);

        // 4. Update Notes database collections safely if requested
        if (request.getNotes() != null) {
            // Flush old records tied to this payment ID
            notesRepository.deleteByPaymentLinkId(id); 
            
            // Populate new records
            request.getNotes().forEach((key, val) -> {
                Notes newNote = new Notes();

                // Match the same clean format generator you have in your POST method:
       // newNote.setNoteId("note_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10));
       newNote.setNoteId(java.util.UUID.randomUUID().toString()); 

        // ---- ADD THIS LINE SO HIBERNATE KNOWS HOW TO PROCESS IT ----
        newNote.setUpdateAction(true);

                newNote.setPaymentLinkId(id);
                newNote.setNoteKey(key);
                newNote.setNoteValue(val);
                notesRepository.save(newNote);
            });
        }

        // 5. Delegate back to our existing retrieval routine to gather compiled tables 
        return this.getPaymentLinkById(id);
    }



    @Override
@Transactional
public PaymentLinkResponse updateUpiPaymentLink(String id, PaymentLinkUpdateRequest request) {
    // 1. Find the core link or fail
    PaymentLink paymentLink = paymentLinkRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("The id provided does not exist"));

    // 2. Guard Check: Enforce that this endpoint exclusively modifies UPI Links
    if (!paymentLink.isUpiLink()) {
        throw new IllegalArgumentException("The id provided does not exist");
    }

    // 3. Status Guard Check: Ensure it is 'created' or 'partially_paid'
    String currentStatus = paymentLink.getStatus() != null ? paymentLink.getStatus().toLowerCase() : "";
    if ("paid".equals(currentStatus) || "cancelled".equals(currentStatus) || "expired".equals(currentStatus)) {
        throw new IllegalArgumentException("update can only be made in created or partially paid state");
    }

    // 4. Partially modify incoming fields if present
    if (request.getReferenceId() != null) {
        paymentLink.setReferenceId(request.getReferenceId());
    }
    if (request.getExpireBy() != null) {
        paymentLink.setExpireBy(request.getExpireBy());
    }
    if (request.getReminderEnable() != null) {
        paymentLink.setReminderEnable(request.getReminderEnable());
    }

    // Update timestamp
    paymentLink.setUpdatedAt(System.currentTimeMillis() / 1000L);
    paymentLinkRepository.save(paymentLink);

    // 5. If notes map is passed, overwrite existing database configurations for this ID
    if (request.getNotes() != null) {
        // Clear old keys to avoid orphan drift rows
        notesRepository.deleteByPaymentLinkId(id);
        
        // Save new notes rows
        request.getNotes().forEach((key, val) -> {
            Notes note = new Notes();
            note.setPaymentLinkId(id);
            note.setNoteKey(key);
            note.setNoteValue(val);
            notesRepository.save(note);
        });
    }

    // 6. Delegate compilation back to your verified retrieval pipeline to ensure nested items return correctly
    return this.getPaymentLinkById(id);
}

/* *
    @Override
    @Transactional
    public PaymentLinkResponse cancelPaymentLink(String id) {
        // 1. Find the link or fail
        PaymentLink paymentLink = paymentLinkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("The id provided does not exist"));

        String status = paymentLink.getStatus().toLowerCase();

        // 2. Razorpay Rule Guard A: Check if already paid or partially paid
        if (status.equals("paid") || status.equals("partially_paid")) {
            throw new IllegalArgumentException("cannot cancel or expire an already paid/partially paid link");
        }

        // 3. Razorpay Rule Guard B: Check if already expired or cancelled
        if (status.equals("expired") || status.equals("cancelled")) {
            throw new IllegalArgumentException("cannot cancel or expire an expired link");
        }

        // 4. Update core state variables
        long currentUnixTime = System.currentTimeMillis() / 1000L;
        paymentLink.setStatus("cancelled");
        paymentLink.setCancelledAt(currentUnixTime);
        paymentLink.setUpdatedAt(currentUnixTime);

        paymentLinkRepository.save(paymentLink);

        // 5. Delegate to retrieval logic to get full stitched table response
        return this.getPaymentLinkById(id);
    }*/


    // Add this method inside your PaymentLinkServiceImpl class

@Override
@Transactional
public PaymentLinkResponse cancelPaymentLink(String id) {
    // 1. Find the link or fail
    PaymentLink paymentLink = paymentLinkRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("The id provided does not exist"));

    // 2. Extract and format status for validation checks
    String currentStatus = paymentLink.getStatus() != null ? paymentLink.getStatus().toLowerCase() : "";

    // 3. Guard Check: Cannot cancel if already paid or partially paid
    if ("paid".equals(currentStatus) || "partially_paid".equals(currentStatus)) {
        throw new IllegalArgumentException("cannot cancel or expire an already paid/partially paid link");
    }

    // 4. Guard Check: Cannot cancel if already expired or cancelled
    if ("expired".equals(currentStatus) || "cancelled".equals(currentStatus)) {
        throw new IllegalArgumentException("cannot cancel or expire an expired link");
    }

    // 5. Apply cancellation changes
    long currentUnixTimestamp = System.currentTimeMillis() / 1000L;
    paymentLink.setStatus("cancelled");
    paymentLink.setCancelledAt(currentUnixTimestamp);
    paymentLink.setUpdatedAt(currentUnixTimestamp);

    // 6. Save modified state to DB
    paymentLinkRepository.save(paymentLink);

    // 7. Return via your verified retrieval compilation pipeline
    return this.getPaymentLinkById(id);
}

}


