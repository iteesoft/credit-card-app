package com.bluenexus.service.impl;

import com.bluenexus.model.*;
import com.bluenexus.repository.TransactionRepository;
import com.bluenexus.repository.UserRepository;
import com.bluenexus.repository.WalletRepository;
import com.bluenexus.service.CardService;
import com.bluenexus.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final UserRepository userRepo;
    private final WalletRepository walletRepo;
    private final TransactionRepository transactionRepo;


    @Override
    public Response getAllTransactions(int userId) {
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        log.info("Fetching "+ user.getName()+ " transactions");
        var transactions = user.getTransactions();
        var total = new BigDecimal("0.00");
        for (Transaction t : transactions) {
            total = total.add(t.getAmount());
        }
        Report report = new Report(transactions, total);
        return Response.builder().statusCode(200).data(Map.of("report", report)).success(true).build();
    }

    @Override
    public Response getMonthlyTransactions(int userId, int month) {
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        var userTransactions = user.getTransactions();
        List<Transaction> monthTransactions = new ArrayList<>();
        var total = new BigDecimal("0.00");
        for (Transaction t: userTransactions) {
            if (t.getDate().getMonthValue() == month) {
                monthTransactions.add(t);
                total = total.add(t.getAmount());
            }
        }
        log.info("Fetching "+ user.getName()+ " transactions for "+ Month.of(month).name());
        Report report = new Report(monthTransactions, total);
        return Response.builder().statusCode(200).developerMessage(user.getName()+ "'s transactions for "+ Month.of(month).name())
                .data(Map.of("report", report)).success(true).build();
    }

    @Transactional
    @Override
    public Response setCreditLimit(int userId, String amount) {
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        Wallet wallet = user.getWallet();
        var created = wallet.getCreatedAt();
        var updated = wallet.getUpdatedAt();
        var today = LocalDate.now();
        //get the current date if the day is first

        if (created.until(today).getDays() > 30 || updated.until(today).getDays() > 30) {
             wallet.setWalletLimit(new BigDecimal(amount));
             wallet.setBalance(new BigDecimal(amount));
             wallet.setUpdatedAt(today);
             resetCreditLimit(userId);
            log.info("Setting new credit limit for "+ user.getName());

        } else {

            //send response to client
            log.info("Unable to Set new credit limit for "+ user.getName());
        }
        return Response.builder().statusCode(200)
                .data(Map.of("wallet", wallet)).success(true).build();
    }

    @Override
    public void resetCreditLimit(int userId) {
        //get the current date if the day is first
        //reset amount spent to zero
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        Wallet wallet = user.getWallet();
        wallet.setAmountSpent(BigDecimal.ZERO);
        wallet.setUpdatedAt(LocalDate.now());
        walletRepo.save(wallet);
    }

    public Transaction createTransaction(String details, String amount) {
        Transaction transaction = new Transaction();
        transaction.setDetails(details);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDate(LocalDate.now());
        return transactionRepo.save(transaction);
    }

    @Transactional
    @Override
    public User makePurchase(int userId, String price) {
        User user = userRepo.findById(userId).orElseThrow(()-> new AppException("User Not Found"));
        List<Transaction> userTransactions = user.getTransactions();
        var userWallet = user.getWallet();
        var priceOfGood = new BigDecimal(price);
        var amountSpent = userWallet.getAmountSpent();
        var limit = userWallet.getWalletLimit();
        var balance = userWallet.getBalance();

        if (!limit.equals(amountSpent) && balance.compareTo(priceOfGood) > 0) {
            userWallet.setAmountSpent(amountSpent.add(priceOfGood));
            userWallet.setBalance(balance.subtract(priceOfGood));
            walletRepo.save(userWallet);
            Transaction t = createTransaction("purchase", price);
            userTransactions.add(t);
            log.info("Purchase of "+price +" made by "+ user.getName());
        } else {
            //send response to client
            log.info("Purchase of "+price +" by "+ user.getName()+ " failed!");
        }
        return user;
    }

    @Scheduled(cron = "0 1 1 * * ?")
    public void resetAllUserCreditLimit() {
        //first day of every month "0 1 1 * ?"
        //reset amount spent to zero
        resetAllCreditLimit();
    }

    @Override
    public void resetAllCreditLimit() {
        //reset amount spent to zero manually
        List<User> users = userRepo.findAll();
        List<Wallet> wallets = new ArrayList<>();
        for (User u: users) {
            wallets.add(u.getWallet());
        }
        for (Wallet w: wallets) {
            w.setAmountSpent(BigDecimal.ZERO);
            w.setBalance(w.getWalletLimit());
            w.setUpdatedAt(LocalDate.now());
            walletRepo.save(w);
            log.info("Reset of all credit cards successful");
        }
    }
}
