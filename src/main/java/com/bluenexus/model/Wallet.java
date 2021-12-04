package com.bluenexus.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Wallet extends Base{

    private BigDecimal walletLimit;
    private BigDecimal amountSpent;
    private BigDecimal balance;
}
