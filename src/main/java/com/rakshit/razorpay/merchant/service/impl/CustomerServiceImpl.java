package com.rakshit.razorpay.merchant.service.impl;

import com.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.rakshit.razorpay.merchant.entity.Customer;
import com.rakshit.razorpay.merchant.entity.Merchant;
import com.rakshit.razorpay.merchant.repository.CustomerRepository;
import com.rakshit.razorpay.merchant.repository.MerchantRepository;
import com.rakshit.razorpay.merchant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {

        if(email == null || email.isBlank()){
            return null;
        }

        return customerRepository.findByMerchant_IdAndEmail(merchantId, email)
                .map(Customer::getId)
                .orElseGet(() -> createNew(merchantId, email, name, phone));
    }

    private UUID createNew(UUID merchantId, String email, String name, String phone){
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        Customer customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .phone(phone)
                .build();

        customer = customerRepository.save(customer);

        log.info("Customer created via findOrCreate id={} merchantId={} email={}",
                customer.getId(), merchantId, email);
        return customer.getId();
    }
}
