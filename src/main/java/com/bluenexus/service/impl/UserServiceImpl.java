package com.bluenexus.service.impl;

import com.bluenexus.dto.UserDto;
import com.bluenexus.exceptions.AppException;
import com.bluenexus.model.User;
import com.bluenexus.model.Wallet;
import com.bluenexus.repository.UserRepository;
import com.bluenexus.repository.WalletRepository;
import com.bluenexus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepo;
    private final WalletRepository walletRepo;

    @Override
    public User save(UserDto request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setWallet(createWallet(request.creditLimit()));
        userRepo.save(user);
        return user;
    }

    @Override
    public Wallet viewWallet(int userId) {
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        log.info("Fetching " + user.getName() +"'s wallet info");
        return user.getWallet();
    }

    @Override
    public User login(UserDto userDto) {
        return null;
    }

    public Wallet createWallet(String amount) {
        Wallet wallet = new Wallet();
        wallet.setAmountSpent(BigDecimal.ZERO);
        wallet.setWalletLimit(new BigDecimal(amount));
        wallet.setBalance(new BigDecimal(amount));
        wallet.setUpdatedAt(LocalDate.now());
        walletRepo.save(wallet);
        return wallet;
    }
}
