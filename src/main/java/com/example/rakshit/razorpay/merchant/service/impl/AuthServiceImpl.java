package com.example.rakshit.razorpay.merchant.service.impl;

import com.example.rakshit.razorpay.common.enums.MerchantStatus;
import com.example.rakshit.razorpay.common.enums.UserRole;
import com.example.rakshit.razorpay.common.exception.DuplicateResourceException;
import com.example.rakshit.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.example.rakshit.razorpay.merchant.dto.response.MerchantResponse;
import com.example.rakshit.razorpay.merchant.entity.AppUser;
import com.example.rakshit.razorpay.merchant.entity.Merchant;
import com.example.rakshit.razorpay.merchant.repository.AppUserRepository;
import com.example.rakshit.razorpay.merchant.repository.MerchantRepository;
import com.example.rakshit.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request){

        //check if merchant with email exists
        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_MERHCANT_EMAIL", "Merchant with email already exists" + request.email());
        }

        Merchant merchant = Merchant.builder()
                .businessName(request.businessName())
                .businessType(request.businessType())
                .email(request.email())
                .name(request.name())
                .status(MerchantStatus.PENDING_KYC)
                .build();

        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .merchant(merchant)
                .email(request.email())
                .passwordHash(request.password()) //TODO: encrypt
                .role(UserRole.OWNER)
                .build();

        appUserRepository.save(appUser);

        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getEmail(),
                merchant.getBusinessName(),
                merchant.getBusinessType(),
                merchant.getStatus()
        );
    }

}
