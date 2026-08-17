package com.rakshit.razorpay.merchant.service.impl;

import com.rakshit.razorpay.common.enums.MerchantStatus;
import com.rakshit.razorpay.common.enums.UserRole;
import com.rakshit.razorpay.common.exception.DuplicateResourceException;
import com.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.rakshit.razorpay.merchant.dto.request.LoginRequest;
import com.rakshit.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.rakshit.razorpay.merchant.dto.response.LoginResponse;
import com.rakshit.razorpay.merchant.dto.response.MerchantResponse;
import com.rakshit.razorpay.merchant.entity.AppUser;
import com.rakshit.razorpay.merchant.entity.Merchant;
import com.rakshit.razorpay.merchant.repository.AppUserRepository;
import com.rakshit.razorpay.merchant.repository.MerchantRepository;
import com.rakshit.razorpay.merchant.security.JwtUtil;
import com.rakshit.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
                .passwordHash(passwordEncoder.encode(request.password()))
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

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser appUser = appUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.email()));

        String token = jwtUtil.generateAccessToken(request.email(), appUser.getMerchant().getId(), appUser.getRole().toString());

        return new LoginResponse(token);
    }

}
