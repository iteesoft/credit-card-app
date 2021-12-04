package com.bluenexus.service;

import com.bluenexus.model.*;

import java.util.List;

public interface CardService {
    Response getAllTransactions(int userId);
    Response getMonthlyTransactions(int userId, int month);
    Response setCreditLimit(int userId, String amount);
    void resetCreditLimit(int userId);
    User makePurchase(int userId, String price);

    void resetAllCreditLimit();
}
